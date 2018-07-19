from django.http import HttpResponseRedirect
from django.template import loader
from django.shortcuts import render, get_object_or_404, redirect
from django.urls import reverse
from django.contrib.auth import authenticate, login, logout
from django.contrib.auth.decorators import login_required
from django.core.paginator import EmptyPage, PageNotAnInteger, Paginator
from django.contrib import messages

from django.contrib.auth.models import User
from .models import Transaction
from django.db.models import Q
from datetime import timedelta, date
import datetime


def login_view(request):
	if request.method == 'POST':
		username = request.POST['username']
		password = request.POST['password']
		user = authenticate(request, username=username, password=password)
		if user is not None:
			login(request, user)
			return HttpResponseRedirect(reverse('core:dashboard_view'))
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
		messages.success(request, 'You have successfully created a new account. Start Shopping!!!')
		return HttpResponseRedirect(reverse('core:login_view'))
	return render(request, 'signup.html')


@login_required(login_url='/')
def dashboard_view(request):
	user_id = request.user.id

	global time_frame
	time_frame = request.GET.get('dropdownMenu')
		
	# Set the time frames
	if time_frame == 'Last week':
		start_date = date.today()
		end_date = start_date - timedelta(days=6)
	elif time_frame == 'Last month':
		start_date = date.today()
		end_date = start_date - timedelta(days=30)
	elif time_frame == 'Last year':
		start_date = date.today()
		end_date = start_date - timedelta(days=90)
	else:
		start_date = date.today()
		end_date = start_date - timedelta(days=6)
		# start_date = datetime.date(time_frame, 12, 31)
		# end_date = datetime.date(time_frame, 1, 1)

	# For dubuging 
	print (start_date)
	print (end_date)

	# Query transactions
	transactions_list = Transaction.objects.filter(
			user_id = user_id,
			created_at__gte=datetime.datetime(end_date.year, end_date.month, end_date.day, 0, 0, 0),
			created_at__lte=datetime.datetime(start_date.year, start_date.month, start_date.day, 23, 59, 59)    		
		)

	# Reverse list to show newer transactions first
	transaction_list = list(reversed(transactions_list))

	page = request.GET.get('page', 1)
	paginator = Paginator(transaction_list, 10)
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
			'total': total
		}
	)

# @login_required(login_url='/')
# def clear_history(request):
# 	user_id = request.user.id
# 	transaction_list = Transaction.objects.filter(user_id=user_id).delete()
	
# 	return render(
# 		request,
# 		'dashboard.html',
# 		{
# 			'transaction_list': transaction_list,
# 			'user': request.user,
# 			'total': total
# 		}
# 	)

@login_required(login_url='/')
def logout_view(request):
	logout(request)
	messages.success(request, 'You have been logged out!')
	return HttpResponseRedirect(reverse('core:login_view'))
	
