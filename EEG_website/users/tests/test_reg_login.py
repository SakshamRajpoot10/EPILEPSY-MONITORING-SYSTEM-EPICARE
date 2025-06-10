from django.test import TestCase
from django.contrib.auth.models import User
from users.forms import UserRegisterForm

# Verify User Registration with Valid Input
class UserRegistrationTest(TestCase):
    def test_user_registration_valid(self):
        form_data = {'username': 'newuser', 'email': 'newuser@example.com', 'password1': 'testpassword123', 'password2': 'testpassword123'}
        form = UserRegisterForm(data=form_data)
        self.assertTrue(form.is_valid())
        
        form.save()
        user = User.objects.get(username='newuser')
        self.assertIsNotNone(user)
        self.assertEqual(user.email, 'newuser@example.com')

#Test Login with Valid Credentials      
class UserLoginTest(TestCase):
    def setUp(self):
        self.credentials = {
            'username': 'testuser',
            'password': 'testpassword'
        }
        User.objects.create_user(**self.credentials)

    def test_login_valid_credentials(self):
        response = self.client.post('/login/', self.credentials, follow=True)
        self.assertTrue(response.context['user'].is_authenticated)
#Check Rejection of Invalid Credentials
class UserInvalidLoginTest(TestCase):
    def setUp(self):
        self.credentials = {
            'username': 'testuser',
            'password': 'testpassword'
        }
        User.objects.create_user(**self.credentials)

    def test_login_invalid_credentials(self):
        wrong_credentials = {
            'username': 'wronguser',
            'password': 'wrongpassword'
        }
        response = self.client.post('/login/', wrong_credentials, follow=True)
        self.assertFalse(response.context['user'].is_authenticated)
