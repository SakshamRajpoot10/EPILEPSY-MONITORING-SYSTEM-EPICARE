from django.test import TestCase
from django.urls import reverse
from django.contrib.auth.models import User
from django.utils.encoding import force_bytes, force_str, DjangoUnicodeDecodeError

from django.utils.http import urlsafe_base64_encode,urlsafe_base64_decode
from users.utils import generate_token
from users.models import Profile
from django.core.files.uploadedfile import SimpleUploadedFile
from io import BytesIO
from PIL import Image
 
# new
class RegisterProfileViewTest(TestCase): 

    def test_register_view(self):
        # Ensure the registration view returns a 200 status code
        response = self.client.get(reverse('register'))
        self.assertEqual(response.status_code, 200)

        # Ensure the registration form is being used
        self.assertContains(response, '<form')
        self.assertContains(response, 'csrfmiddlewaretoken')

    def test_successful_registration(self):
        # Ensure a new user is created upon successful registration
        data = {
            'username': 'testuser',
            'password1': 'testpassword123',
            'password2': 'testpassword123',
            'email': 'testuser@example.com',   
        }
        response = self.client.post(reverse('register'), data, follow=True)
         
        self.assertRedirects(response, reverse('profile'))

        # Check if the user is created        
        self.assertTrue(User.objects.filter(username='testuser').exists())

class ProfileViewTest(TestCase):
    def setUp(self):
        # Create a user for testing
        self.user = User.objects.create_user(username='testuser', password='testpassword123')
         
    def test_profile(self):
        # Log in the user
        self.client.login(username='testuser', password='testpassword123')

        # Create a sample image file to upload
        image = Image.new('RGB', (100, 100))
        image_io = BytesIO()
        image.save(image_io, 'jpeg')
        image_io.seek(0)
        image_file = SimpleUploadedFile('test_image.jpg', image_io.read(), content_type='image/jpeg')

        # Ensure the profile is updated upon successful form submission
        data = {
            'first_name': 'Test',
            'last_name': 'User',
            'image': image_file,
        }
        response = self.client.post(reverse('profile'), data, follow=True)
        # Redirect after successful profile update
        self.assertEqual(response.status_code, 200)

        # Check if the profile is updated
        user = User.objects.get(username='testuser')
        # self.assertEqual(user.first_name, 'Test')
        # self.assertEqual(user.last_name, 'User')
        
        # Check the image field in the Profile model
        profile = user.profile
        self.assertNotEqual(profile.image.name, 'profile_pics/default.jpg')  # Image should not be the default


class BaseTest(TestCase):
    def setUp(self):
        self.register_url=reverse('register')
        self.login_url=reverse('login')
        self.user={
            'email':'testemail@gmail.com',
            'username':'username',
            'password':'password',
            'password2':'password',
            'name':'fullname'
        }
        self.user_short_password={
            'email':'testemail@gmail.com',
            'username':'username',
            'password':'tes',
            'password2':'tes',
            'name':'fullname'
        }
        self.user_unmatching_password={

            'email':'testemail@gmail.com',
            'username':'username',
            'password':'teslatt',
            'password2':'teslatto',
            'name':'fullname'
        }

        self.user_invalid_email={
            
            'email':'test.com',
            'username':'username',
            'password':'teslatt',
            'password2':'teslatto',
            'name':'fullname'
        }
        return super().setUp()

class RegisterTest(BaseTest):
   def test_can_view_page_correctly(self):
       response=self.client.get(self.register_url)
       self.assertEqual(response.status_code, 200)
       self.assertTemplateUsed(response,'users/register.html')
 


class LoginTest(BaseTest):
    def test_can_access_page(self):
        response=self.client.get(self.login_url)
        self.assertEqual(response.status_code,200)
        self.assertTemplateUsed(response,'users/login.html') 



class LoginTest(TestCase):
    def setUp(self):
        # Create a user for testing
        self.user = User.objects.create_user(username='testuser', password='testpassword123')

        # URL names for login and logout views
        self.login_url = reverse('login')
        self.logout_url = reverse('logout')

    def test_can_access_page(self):
        # Ensure that the login page is accessible
        response = self.client.get(self.login_url)
        self.assertEqual(response.status_code, 200)
        self.assertTemplateUsed(response, 'users/login.html')

    def test_login_success(self):
        # Attempt to log in with valid credentials
        login_data = {
            'username': 'testuser',
            'password': 'testpassword123',
        }
        response = self.client.post(self.login_url, login_data, format='text/html')

        # Check if the login was successful (expect a redirect status code)
        self.assertEqual(response.status_code, 302)

        # You can also check if the user is logged in after successful login
        self.assertTrue(self.client.login(username='testuser', password='testpassword123'))

 