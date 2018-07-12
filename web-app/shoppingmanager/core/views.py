from django.http import HttpResponseRedirect, HttpResponse
from django.template import loader
from django.shortcuts import render, get_object_or_404, render_to_response
from django.urls import reverse
from django.contrib.auth import authenticate, login, logout
from django.contrib.auth.decorators import login_required
from django.contrib.auth.models import User
from .models import Transaction
from django.db.models import Q

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
		return HttpResponseRedirect(reverse('core:login_view'))
	return render(request, 'signup.html')

@login_required(login_url='/')
def dashboard_view(request):
	user_id = request.user.id
	transactions_list = Transaction.objects.filter(user_id=user_id)
	return render(
		request,
		'dashboard.html',
		{
			'transactions_list': transactions_list,
			'user': request.user
		}
	)
	
@login_required(login_url='/')
def logout_view(request):
	logout(request)
	return render(request, 'login.html', {'logout_message': "You have been logged out!"})
