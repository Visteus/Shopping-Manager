from rest_framework.serializers import CharField, EmailField, HyperlinkedIdentityField, ModelSerializer, ValidationError, SerializerMethodField
from django.contrib.auth.models import User
from .models import Transaction
from django.db.models import Q

class UserSerializer(ModelSerializer):
	class Meta:
		model = User
		fields = ('id', 'username', 'password', 'email', 'first_name', 'last_name')
		
class TransactionSerializer(ModelSerializer):
	class Meta:
		model = Transaction
		fields = ('id', 'user', 'title', 'total', 'created_at')

class UserLoginSerializer(ModelSerializer):
	token = CharField(allow_blank=True, read_only=True)
	username = CharField(required=True, allow_blank=False)
	class Meta:
		model = User
		fields = ('username', 'password', 'token')
		extra_kwargs = {"password": {"write_only": True}}
	
	def validate(self, data):
		user_obj = None
		username = data.get("username", None)
		password = data["password"]

		if not username:
			raise ValidationError("A username or password is required to login.")
		
		user = User.objects.filter(
			Q(username=username)
		).distinct()

		if user.exists() and user.count() == 1:
			user_obj = user.first()
		else:
			raise ValidationError("This username is not valid")
		
		if user_obj:
			if not user_obj.check_password(password):
				raise ValidationError("Incorrect credentials. Please try again!")
		data["token"] = "TOKEN"
		return data