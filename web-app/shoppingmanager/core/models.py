from django.db import models
from django.contrib.auth.models import User

# Create your models here.
class Transaction(models.Model):
	user = models.ForeignKey(User, on_delete=models.CASCADE)
	name = models.CharField(max_length=150)
	total = models.DecimalField(max_digits=12, decimal_places=2)
	# auto_now_add vs Datetime.now()
	pub_date = models.DateTimeField(auto_now_add=True)

	def __str__(self):
		return "%s %d" % (self.name, self.total)