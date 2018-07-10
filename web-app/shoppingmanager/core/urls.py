from django.urls import path

from . import views

app_name = 'core'

urlpatterns = [
	path('', views.login_view, name='login_view'),
	path('dashboard/', views.dashboard_view, name='dashboard_view'),
	# path('signup/', views.sign_up, name='sign_in'),
	path('logout/', views.logout_view, name='logout_view')
]