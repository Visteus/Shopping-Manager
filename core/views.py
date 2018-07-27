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

# REST
from rest_framework import viewsets, authentication, permissions
from rest_framework.generics import (
	CreateAPIView,
	ListAPIView,
	UpdateAPIView,
	RetrieveAPIView,
	RetrieveUpdateAPIView
)
from rest_framework.response import Response
from rest_framework.views import APIView
from rest_framework.status import HTTP_200_OK, HTTP_400_BAD_REQUEST
from rest_framework.permissions import IsAuthenticated, AllowAny, IsAuthenticatedOrReadOnly

# Model
from django.contrib.auth.models import User
from .models import Transaction
from django.db.models import Q
from .serializers import TransactionSerializer, UserSerializer, UserLoginSerializer


def login_view(request):
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


def signup_view(request):
	if request.method == 'POST':
		username = request.POST['username']
		password = request.POST['password']
		email = request.POST['email']
		first_name = request.POST['firstname']
		last_name = request.POST['lastname']
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
	# time_frame = 'last-week'
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

	transaction_list = list(reversed(transaction_list))

	# Pagination
	page = request.GET.get('page', 1)
	paginator = Paginator(transaction_list, 4)
	try:
		transactions = paginator.page(page)
	except PageNotAnInteger:
		transactions = paginator.page(1)
	except EmptyPage:
		transactions = paginator.page(paginator.num_pages)

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
def logout_view(request):
	logout(request)
	messages.success(request, 'You have been logged out!')
	return HttpResponseRedirect(reverse('core:login_view'))


# RESTful API
class TransactionView(viewsets.ModelViewSet):
	permission_classes = [IsAuthenticated]
	queryset = Transaction.objects.all()
	serializer_class = TransactionSerializer


class UserView(viewsets.ModelViewSet):
	permission_classes = [IsAuthenticated]
	queryset = User.objects.all()
	serializer_class = UserSerializer


class UserLoginAPIView(APIView):
	permission_classes = [AllowAny]
	serializer_class = UserLoginSerializer
	
	def post(self, request, *args, **kwargs):
		data = request.data
		serializer = UserLoginSerializer(data=data)
		if serializer.is_valid(raise_exception=True):
			new_data = serializer.data
			return Response(new_data, status=HTTP_200_OK)
		return Response(serializer.errors, status=HTTP_400_BAD_REQUEST)


def jwt_response_payload_handler(token, user=None, request=None):
    return {
        'token': token,
        'user': user.id
    }
