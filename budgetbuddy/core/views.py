from django.http import HttpResponseRedirect, HttpResponse, JsonResponse
from django.template import loader
from django.shortcuts import render, get_object_or_404, render_to_response
from django.urls import reverse
from django.contrib.auth import authenticate, login, logout
from django.contrib.auth.decorators import login_required
from django.core.paginator import EmptyPage, PageNotAnInteger, Paginator
from django.contrib.auth.models import User
from .models import Transaction
from django.db.models import Q
from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework import serializers, authentication, permissions

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
		return render(request, 'login.html', {'signup_successful_message': "You've created a new account successfully!"})
	return render(request, 'signup.html')


@login_required(login_url='/')
def dashboard_view(request):
	user_id = request.user.id
	transaction_list = Transaction.objects.filter(user_id=user_id)
	page = request.GET.get('page', 1)
	paginator = Paginator(transaction_list, 20)
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


@login_required(login_url='/')
def logout_view(request):
	logout(request)
	return render(request, 'login.html', {'logout_message': "You have been logged out!"})


# fetch transactions for graph (now w/ REST framework)
class ChartData(APIView):
    
	authentication_classes = (authentication.SessionAuthentication,)
	permission_classes = (permissions.IsAuthenticated,)

	def get(self, request, format=None):
		user_id = self.request.user.id
		transaction_list = Transaction.objects.filter(user =user_id)
		total_list = []
		name_list = []
		date_list = []
		total = 0
		for transaction in transaction_list:
		    total_list.append(transaction.total)
		    name_list.append(transaction.title)
		    date_list.append(transaction.created_at.strftime("%B %d, %Y, %I:%M %p"))

		data = {
			"user": user_id,
			"labels": name_list,
			"totals": total_list,
			"dates": date_list,
		}
		
		return Response(data)

	
