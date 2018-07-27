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
from .serializers import TransactionSerializer, UserSerializer, UserLoginSerializer
import json
from django.core.serializers.json import DjangoJSONEncoder

# REST
from rest_framework.decorators import api_view
from rest_framework.views import APIView
from rest_framework import serializers, authentication, permissions, viewsets
from rest_framework.response import Response
from rest_framework.status import HTTP_200_OK, HTTP_400_BAD_REQUEST
from rest_framework.permissions import IsAuthenticated, AllowAny, IsAuthenticatedOrReadOnly
from rest_framework.authentication import SessionAuthentication, BasicAuthentication
from rest_framework.generics import (
	CreateAPIView,
	ListAPIView,
	UpdateAPIView,
	RetrieveAPIView,
	RetrieveUpdateAPIView
)

# Models
from django.contrib.auth.models import User
from .models import Transaction

# Handle User login 
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

# Handle New User request
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
		messages.success(request, 'You have successfully created a new account. Start Shopping!!!')
		return HttpResponseRedirect(reverse('core:login_view'))
	return render(request, 'signup.html')

# Dashboard view queries transactions given a time frame
@login_required(login_url='/')
def dashboard_view(request, slug):
	user_id = request.user.id	
	time_frame = slug
	# Writen in dashboard_view
	# Read in ChartData
	global transaction_list	

	# Set the time frames
	if time_frame == 'last-week':
		start_date = date.today()
		end_date = start_date - timedelta(days=6)
	elif time_frame == 'last-month':
		start_date = date.today()
		end_date = start_date - timedelta(days=30)
	else:
		start_date = date.today()
		end_date = start_date - timedelta(days=365)

	# Query transactions
	transaction_list = Transaction.objects.filter(
		user_id=user_id,
		created_at__gte=datetime(end_date.year, end_date.month, end_date.day, 0, 0, 0),
    	created_at__lte=datetime(start_date.year, start_date.month, start_date.day, 23, 59, 59)
	)

	graph_labels = []
	transaction_total_list = []
	transaction_date_list = []
	if time_frame == 'last-week':
		for transaction in transaction_list:
			if transaction.created_at.date().strftime('%A %b %d') not in graph_labels:
				graph_labels.append(transaction.created_at.date().strftime('%A %b %d'))
				transaction_total_list.append(0)

			index = graph_labels.index(transaction.created_at.date().strftime('%A %b %d'))
			transaction_total_list[index] += transaction.total

		transaction_date_list = graph_labels
		json_graph_labels = json.dumps(graph_labels)
		json_transaction_total_list = json.dumps(transaction_total_list, cls=DjangoJSONEncoder)
		json_transaction_date_list = json.dumps(transaction_date_list)

	elif time_frame == 'last-month':
		for transaction in transaction_list:
			if transaction.created_at.date().strftime('%b %d') not in graph_labels:
				graph_labels.append(transaction.created_at.date().strftime('%b %d'))
				transaction_total_list.append(0)

			index = graph_labels.index(transaction.created_at.date().strftime('%b %d'))
			transaction_total_list[index] += transaction.total

		transaction_date_list = graph_labels
		json_graph_labels = json.dumps(graph_labels)
		json_transaction_total_list = json.dumps(transaction_total_list, cls=DjangoJSONEncoder)
		json_transaction_date_list = json.dumps(transaction_date_list)

	else:
		for transaction in transaction_list:
			if transaction.created_at.date().strftime('%b') not in graph_labels:
				graph_labels.append(transaction.created_at.date().strftime('%b'))
				transaction_total_list.append(0)

			index = graph_labels.index(transaction.created_at.date().strftime('%b'))
			transaction_total_list[index] += transaction.total

		transaction_date_list = graph_labels
		json_graph_labels = json.dumps(graph_labels)
		json_transaction_total_list = json.dumps(transaction_total_list, cls=DjangoJSONEncoder)
		json_transaction_date_list = json.dumps(transaction_date_list)


	print (transaction_total_list)


	# Organize list from most recent to older
	transaction_list = list(reversed(transaction_list))

	# Handle pagination (10 transactions per page)
	page = request.GET.get('page', 1)
	paginator = Paginator(transaction_list, 10)
	try:
		transactions = paginator.page(page)
	except PageNotAnInteger:
		transactions = paginator.page(1)
	except EmptyPage:
		transactions = paginator.page(paginator.num_pages)

	# Calculate total of combined transactions
	total = 0
	for transaction in transaction_list:
		total += transaction.total

	# Return
	return render(
		request,
		'dashboard.html',
		{
			'transactions': transactions,
			'user': request.user,
			'total': total,
			'time_frame': time_frame,
			'json_graph_labels': json_graph_labels,
            'json_transaction_total_list': json_transaction_total_list,
            'json_transaction_date_list': json_transaction_date_list
		}
	)


# Fetch transactions for graph (now w/ REST framework)
class ChartData(APIView):
    
	authentication_classes = (authentication.SessionAuthentication,)
	permission_classes = (permissions.IsAuthenticated,)

	def get(self, request, format=None):
		user_id = self.request.user.id

		# Name list not included (Display dates instead)
		total_list = []
		# name_list = []
		date_list = []
		total = 0

		for transaction in transaction_list:
		    total_list.append(transaction.total)
		    # name_list.append(transaction.title)
		    date_list.append(transaction.created_at.strftime("%m/%d"))

		data = {
			"user": user_id,
			# "labels": name_list,
			"totals": total_list,
			"dates": date_list,
		}		
		return Response(data)

# Handle logout request
@login_required(login_url='/')
def logout_view(request):
	logout(request)
	return render(request, 'login.html', {'logout_message': "You have been logged out!"})


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
	
