from django.test import TestCase
from users.forms import UserRegisterForm, UserUpdateForm, ProfileUpdateForm

class TestForms(TestCase):
    def test_user_register_form_valid(self):
        form_data = {'username': 'testuser', 'email': 'test@example.com', 'password1': 'complexpassword', 'password2': 'complexpassword'}
        form = UserRegisterForm(data=form_data)
        self.assertTrue(form.is_valid())

    def test_user_update_form_valid(self):
        form_data = {'username': 'updateduser', 'email': 'updated@example.com'}
        form = UserUpdateForm(data=form_data)
        self.assertTrue(form.is_valid())

    def test_profile_update_form_valid(self):
        form_data = {'image': 'path/to/image.jpg'}
        form = ProfileUpdateForm(data=form_data)
        self.assertTrue(form.is_valid())
