from rest_framework.serializers import ModelSerializer
from django.contrib.auth.models import User
from .models import Transaction
from django.db.models import Q

class UserSerializer(ModelSerializer):
	class Meta:
		model = User
		fields = ('id', 'username', 'password', 'email', 'first_name')

	def create(self, validated_data):
		user = User(
			username=validated_data['username'],
			email=validated_data['email']
		)
		user.set_password(validated_data['password'])
		user.save()
		return user
		
class TransactionSerializer(ModelSerializer):
	class Meta:
		model = Transaction
		fields = ('id', 'user', 'title', 'total', 'created_at')
		
