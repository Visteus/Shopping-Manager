# Django Utilities
from django.http import HttpResponseRedirect
from django.template import loader
from django.shortcuts import render, get_object_or_404, redirect
from django.urls import reverse
from django.contrib.auth import authenticate, login, logout
from django.contrib.auth.decorators import login_required
from django.core.paginator import EmptyPage, PageNotAnInteger, Paginator
from django.contrib import messages
from datetime import timedelta, date, datetime
import json
from django.contrib.admin.views.decorators import staff_member_required

# REST
from rest_framework import viewsets, authentication, permissions
from rest_framework.permissions import IsAuthenticated, AllowAny
from rest_framework.response import Response
# Model
from django.contrib.auth.models import User
from .models import Transaction
from django.db.models import Q
from .serializers import TransactionSerializer, UserSerializer


def login_view(request):
	if not request.user.is_authenticated:
		if request.method == 'POST':
			username = request.POST['username']
			password = request.POST['password']
			user = authenticate(request, username=username, password=password)
			if user is not None:
				login(request, user)
				return HttpResponseRedirect(reverse('core:dashboard_view', kwargs={'slug': 'last-week'}))
			else:
				return render(request, 'login.html', {'error_message': "Username or password incorrect"})
		return render(request, 'login.html')
	else:
		return HttpResponseRedirect(reverse('core:dashboard_view', kwargs={'slug': 'last-week'}))


def signup_view(request):
	if request.method == 'POST':
		username = request.POST['username']
		password = request.POST['password']
		email = request.POST['email']
		first_name = request.POST['firstname']
		last_name = request.POST['lastname']
		if User.objects.filter(username=username).exists():
			messages.error(request, 'Username is taken. Please try something else!')
		elif User.objects.filter(email=email).exists():
			messages.error(request, 'Email is taken. Please try something else!')
		else:
			user = User.objects.create_user(
				username=username,
				password=password,
				email=email,
				first_name=first_name,
				last_name=last_name
			)
			user.save()
			messages.success(request, 'You have been created a new account successfully!')
			return HttpResponseRedirect(reverse('core:login_view'))
	return render(request, 'signup.html')


@login_required(login_url='/')
def dashboard_view(request, slug):
	user_id = request.user.id
	time_frame = slug
	# Set the time frames
	if time_frame == 'last-month':
		start_date = date.today()
		end_date = start_date - timedelta(days=30)
		time_frame = 'Last Month'
	elif time_frame == 'last-year':
		start_date = date.today()
		end_date = start_date - timedelta(days=365)
		time_frame = 'Last Year'
	else:
		start_date = date.today()
		end_date = start_date - timedelta(days=6)
		time_frame = 'Last Week'

	transaction_list = Transaction.objects.filter(
		user_id=user_id,
		created_at__gte=datetime(end_date.year, end_date.month, end_date.day, 0, 0, 0),
    	created_at__lte=datetime(start_date.year, start_date.month, start_date.day, 23, 59, 59)
	)

	# Graph
	graph_xAxis = []
	graph_column_total = []
	for transaction in transaction_list:
		if transaction.created_at.date().strftime('%b %d') not in graph_xAxis:
			graph_xAxis.append(transaction.created_at.date().strftime('%b %d'))
			graph_column_total.append(transaction.total)
		else:
			graph_column_total[graph_xAxis.index(transaction.created_at.date().strftime('%b %d'))] += transaction.total
	json_graph_xAxis = json.dumps(graph_xAxis)
	json_graph_column_total = json.dumps(['{:.2f}'.format(x) for x in graph_column_total])

	# transaction_list = list(reversed(transaction_list))

	# Pagination
	page = request.GET.get('page', 1)
	paginator = Paginator(transaction_list, 10)
	try:
		transactions = paginator.page(page)
	except PageNotAnInteger:
		transactions = paginator.page(1)
	except EmptyPage:
		transactions = paginator.page(paginator.num_pages)

	# total of this timeframe
	total = 0
	for transaction in transaction_list:
		total += transaction.total

	return render(
		request,
		'dashboard.html',
		{ 
			'transactions': transactions,
			'user': request.user,
			'total': total,
			'time_frame': time_frame,
			'json_graph_xAxis': json_graph_xAxis,
			'json_graph_column_total': json_graph_column_total,
		}
	)


@login_required(login_url='/')
def delete_history(request):
	user_id = request.user.id
	Transaction.objects.filter(user_id=user_id).delete()
	return HttpResponseRedirect(reverse('core:dashboard_view', kwargs={'slug': 'last-week'}))

@login_required(login_url='/')
def logout_view(request):
	logout(request)
	messages.success(request, 'You have been logged out!')
	return HttpResponseRedirect(reverse('core:login_view'))


# RESTful API
# @staff_member_required
class TransactionView(viewsets.ModelViewSet):
	permission_classes = [IsAuthenticated]
	queryset = Transaction.objects.all()
	serializer_class = TransactionSerializer

# @staff_member_required
class UserView(viewsets.ModelViewSet):
	permission_classes = [AllowAny]
	queryset = User.objects.all()
	serializer_class = UserSerializer
	def create(request,self, validated_data):
		user = User(
			username=validated_data['username'],
			email=validated_data['email']
		)
		user.set_password(validated_data['password'])
		user.save()
		return user
		

def jwt_response_payload_handler(token, user=None, request=None):
    return {
        'token': token,
        'user': user.id
    }
