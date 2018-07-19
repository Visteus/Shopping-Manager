from django.urls import path
# from rest_framework_jwt.views import obtain_jwt_token
from django.conf.urls import url, include
from django.contrib.auth.models import User
from rest_framework import routers, serializers, viewsets
from rest_framework_jwt.views import obtain_jwt_token

from . import views

app_name = 'core'
router = routers.DefaultRouter()
router.register('transactions', views.TransactionView)
router.register('users', views.UserView)
# router.register('login', views.UserLoginAPIView)

urlpatterns = [
	path('', views.login_view, name='login_view'),
	path('dashboard/', views.dashboard_view, name='dashboard_view'),
	path('signup/', views.signup_view, name='signup_view'),
	path('logout/', views.logout_view, name='logout_view'),
	# API
	path('api/', include(router.urls)),
	# url(r'^api-auth/', include('rest_framework.urls', namespace='rest_framework')),
	url(r'^api-login/$', views.UserLoginAPIView.as_view(), name='login'),
	url(r'^api-token-auth/', obtain_jwt_token),
]
