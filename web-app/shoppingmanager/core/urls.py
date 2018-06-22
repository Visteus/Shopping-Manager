from django.urls import path

from . import views

app_name = 'core'

urlpatterns = [
	path('', views.login_view, name='login_view'),
]