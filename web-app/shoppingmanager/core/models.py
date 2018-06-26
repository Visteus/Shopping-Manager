from django.db import models
from django.contrib.auth.models import User

# User model same as contact manager?

# Simple table with 5 columns.
# One User to many Transactions.
# Deletes all transactions associated with a User when that User gets deleted.
class Transactions(models.Model):
	name = models.CharField(max_length=50)
	total = models.DecimalField(max_digits=12, decimal_places=2)
	time = models.DateTimeField(auto_now_add=True)
	userKey = models.ForeignKey(User, on_delete=models.CASCADE)
