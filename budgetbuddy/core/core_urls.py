from django.urls import path, re_path


from .views import login_view, dashboard_view, signup_view, logout_view, ChartData

app_name = 'core'

urlpatterns = [
	path('', login_view, name='login_view'),
	path('dashboard/', dashboard_view, name='dashboard_view'),
	path('signup/', signup_view, name='signup_view'),
	path('logout/', logout_view, name='logout_view'),
	re_path(r'^api/chart/data/$', ChartData.as_view(), name='get_data'),
]
