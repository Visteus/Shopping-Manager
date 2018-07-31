from django.contrib.auth.models import AnonymousUser, User
from django.test import TestCase, RequestFactory
from django.test import TestCase

from core.models import Transaction
from django.contrib.auth.models import User


class SimpleTest(TestCase):
    def setUp(self):
        # Every test needs access to the request factory.
        self.factory = RequestFactory()

    def test_details(self):
        # Create an instance of a GET request.
        request = self.factory.get('/')
        request.user = AnonymousUser()


class TransactionModelTest(TestCase):
    @classmethod
    def setUpTestData(cls):
        User.objects.create(username='admin', password='admin123')
        Transaction.objects.create(user=User.objects.create(username='test_buddy', password='admin123', email='test_buddy@bb.com'),
                                   title='Groceries', total='99.99')

    def test_title_label(self):
        transaction = Transaction.objects.get(id=1)
        field_label = transaction._meta.get_field('title').verbose_name
        self.assertEquals(field_label, 'title')

    def test_total_label(self):
        transaction = Transaction.objects.get(id=1)
        field_label = transaction._meta.get_field('total').verbose_name
        self.assertEquals(field_label, 'total')

    def test_title_max_length(self):
        transaction = Transaction.objects.get(id=1)
        max_length = transaction._meta.get_field('title').max_length
        self.assertEquals(max_length, 150)

    def test_username(self):
        transaction = Transaction.objects.get(id=1)
        user = transaction.user
        username = user.username
        self.assertEquals(username, 'test_buddy')

    def test_email(self):
        transaction = Transaction.objects.get(id=1)
        user = transaction.user
        email = user.email
        self.assertEquals(email, 'test_buddy@bb.com')

    def test_title(self):
        transaction = Transaction.objects.get(id=1)
        title = transaction.title
        self.assertEquals(title, 'Groceries')

    def test_total(self):
        transaction = Transaction.objects.get(id=1)
        total = float(transaction.total)
        self.assertEquals(total, 99.99)
