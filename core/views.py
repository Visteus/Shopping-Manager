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
	# If a user is authenticated which in this case is the user has not logged out yet from last session, redirect to dashboard with last-week param for graph and transaction table.
	# If a user is not authenticated, accept and authenticate username and password from request.POST. Then, redirect to dashboard with last-week param or keyword arguments
	# Slug: https://stackoverflow.com/questions/427102/what-is-a-slug-in-django.
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
	# Accept username, password, email, and first name from request.POST
	# Check if the username or password is not taken
	# Create and save new user with validated data
	# Use Django message framework to print out successful message and go back to login page
	if request.method == 'POST':
		username = request.POST['username']
		password = request.POST['password']
		email = request.POST['email']
		first_name = request.POST['firstname']
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
			)
			user.save()
			messages.success(request, 'You have been created a new account successfully!')
			return HttpResponseRedirect(reverse('core:login_view'))
	return render(request, 'signup.html')


@login_required(login_url='/')
def dashboard_view(request, slug):
	# Login is required from this point
	# Dashboard accepts slug that will be used to sort timeframe
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

	# Sort transaction list by userid and created time
	transaction_list = Transaction.objects.filter(
		user_id=user_id,
		created_at__gte=datetime(end_date.year, end_date.month, end_date.day, 0, 0, 0),
    	created_at__lte=datetime(start_date.year, start_date.month, start_date.day, 23, 59, 59)
	)

	# Graph
	# Reverse transaction list in order to let graph use chronological timeline
	reversed_transaction_list = list(reversed(transaction_list))
	graph_xAxis = []
	# Graph dataset
	graph_column_total = []
	for transaction in reversed_transaction_list:
		if transaction.created_at.date().strftime('%b %d') not in graph_xAxis:
			graph_xAxis.append(transaction.created_at.date().strftime('%b %d'))
			graph_column_total.append(transaction.total)
		else:
			graph_column_total[graph_xAxis.index(transaction.created_at.date().strftime('%b %d'))] += transaction.total

	# Convert python list to JSON that Javascript can read from templates
	json_graph_xAxis = json.dumps(graph_xAxis)
	json_graph_column_total = json.dumps(['{:.2f}'.format(x) for x in graph_column_total])

	# Pagination
	page = request.GET.get('page', 1)
	# How many items per page
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
# Delete the user's transactions
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
# Only authenticated user could send requests to create transactions
class TransactionView(viewsets.ModelViewSet):
	permission_classes = [IsAuthenticated]
	queryset = Transaction.objects.all()
	serializer_class = TransactionSerializer
	http_method_names = ['post', 'head', 'options']

# Anyone can create a new user from signup of client
class UserView(viewsets.ModelViewSet):
	permission_classes = [AllowAny]
	queryset = User.objects.all()
	serializer_class = UserSerializer
	http_method_names = ['post', 'head', 'options']


# return a token and userid on client side that will be used to log in, create new transactions
# JSON Web Token Authentication: http://getblimp.github.io/django-rest-framework-jwt/
def jwt_response_payload_handler(token, user=None, request=None):
    return {
        'token': token,
        'user': user.id
    }
