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
			return render(
				request,
				'login.html',
				{
					'error_message': "You are not logged in. Please log in!"
				}
			)
	return render(request, 'login.html')


@login_required(login_url='')
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


@login_required(login_url='')
def logout_view(request):
	logout(request)
	return render(request, 'logout.html')
