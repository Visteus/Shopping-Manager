from django.shortcuts import render
from django.http import HttpResponseRedirect
from django.template import loader

def login_view(request):
	return render(request, 'login.html')
