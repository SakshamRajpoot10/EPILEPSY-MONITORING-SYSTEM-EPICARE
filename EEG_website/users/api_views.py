from django.contrib.auth.models import User
from django.contrib.auth import authenticate
from django.urls import reverse
import numpy as np
from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework.decorators import api_view
from rest_framework.permissions import IsAuthenticated
from rest_framework import status
from django.core.mail import send_mail
from django.conf import settings
from rest_framework.permissions import AllowAny
from .models import Profile
from .serializers import UserSerializer, ProfileSerializer
from rest_framework.authtoken.models import Token

@api_view(['GET'])
def api_root(request, format=None):
    """
    API Root: Lists all available API endpoints.
    """
    return Response({
        'register': reverse('api-register', request=request, format=format),
        'login': reverse('api-login', request=request, format=format),
        'profile': reverse('api-profile', request=request, format=format),
        'logout': reverse('api-logout', request=request, format=format),
    })

# Register API
class RegisterAPI(APIView):
    permission_classes = [AllowAny]  # This allows access without authentication.

    def post(self, request):
        data = request.data
        username = data.get('username')
        email = data.get('email')
        password = data.get('password')

        if User.objects.filter(username=username).exists():
            return Response({"error": "Username already exists"}, status=status.HTTP_400_BAD_REQUEST)

        user = User.objects.create_user(username=username, email=email, password=password)
        Token.objects.create(user=user)  # Create Token
        return Response({"message": "User registered successfully"}, status=status.HTTP_201_CREATED)


# Login API
class LoginAPI(APIView):
    def post(self, request):
        data = request.data
        username = data.get('username')
        password = data.get('password')

        user = authenticate(username=username, password=password)
        if user:
            token, _ = Token.objects.get_or_create(user=user)
            return Response({
                "message": "Login successful",
                "token": token.key,
                "user": UserSerializer(user).data
            }, status=status.HTTP_200_OK)
        return Response({"error": "Invalid credentials"}, status=status.HTTP_401_UNAUTHORIZED)


# Change Password API
class ChangePasswordAPI(APIView):
    permission_classes = [IsAuthenticated]

    def post(self, request):
        user = request.user
        data = request.data
        old_password = data.get('old_password')
        new_password = data.get('new_password')

        if not user.check_password(old_password):
            return Response({"error": "Old password is incorrect"}, status=status.HTTP_400_BAD_REQUEST)

        user.set_password(new_password)
        user.save()
        return Response({"message": "Password changed successfully"}, status=status.HTTP_200_OK)


# Forgot Password API
@api_view(['POST'])
def forgot_password(request):
    email = request.data.get('email')
    if not User.objects.filter(email=email).exists():
        return Response({"error": "User with this email does not exist"}, status=status.HTTP_400_BAD_REQUEST)

    user = User.objects.get(email=email)
    new_password = User.objects.make_random_password()
    user.set_password(new_password)
    user.save()

    # Send email with new password
    send_mail(
        'Password Reset Request',
        f'Your new password is: {new_password}',
        settings.DEFAULT_FROM_EMAIL,
        [email],
        fail_silently=False,
    )

    return Response({"message": "New password has been sent to your email"}, status=status.HTTP_200_OK)


# Update Profile API
class UpdateProfileAPI(APIView):
    permission_classes = [IsAuthenticated]

    def put(self, request):
        user = request.user
        profile = Profile.objects.get(user=user)

        # Update user data
        user_data = request.data.get('user')
        if user_data:
            user.username = user_data.get('username', user.username)
            user.email = user_data.get('email', user.email)
            user.first_name = user_data.get('first_name', user.first_name)
            user.last_name = user_data.get('last_name', user.last_name)
            user.save()

        # Update profile data
        profile_data = request.data.get('profile')
        if profile_data:
            profile.image = profile_data.get('image', profile.image)
            profile.save()

        return Response({
            "message": "Profile updated successfully",
            "user": UserSerializer(user).data,
            "profile": ProfileSerializer(profile).data
        }, status=status.HTTP_200_OK)
    
 
class PredictDataAPIView(APIView):

    def get(self, request, *args, **kwargs):
        """
        Handle GET request.
        Just return a message or some form for the user to input data.
        """
        return Response({
            "message": "Please send a POST request with the required data for prediction.",
            "example": {
                "patient_id": "123",
                "sendor_data": "5.7",
                "actual_class": "1"
            }
        }, status=status.HTTP_200_OK)

    def post(self, request, *args, **kwargs):
        """
        Handle POST request for prediction.
        Extract data from the request, process it, and return the prediction and suggestion.
        """
        try:
            # Extract form data and handle missing or invalid data
            # patient_id = request.data.get('patient_id')
            sendor_data = request.data.get('sendor_data')
            actual_class = request.data.get('actual_class')

            # Check if all required fields are provided
            if not sendor_data or not actual_class:
                return Response(
                    {"error": "Missing required fields"},
                    status=status.HTTP_400_BAD_REQUEST
                )

            # Convert sendor_data to float (handle case where it might be empty or invalid)
            sendor_data = float(sendor_data)
            actual_class = float(actual_class)

            # Create input array for prediction (reshape as necessary)
            input_data = np.array([sendor_data]).reshape(1, -1)  # Reshaping to match model input format

            # Make the prediction (Currently using a mock prediction)
            prediction = [0]  # This should be: model.predict(input_data)

            # Set prediction label and suggestion
            if prediction[0] == 1:
                prediction_label = 'Epilepsy Detected'
                suggestion = "Your epilepsy cycle pattern suggests optimal care periods."
            else:
                prediction_label = 'Epilepsy Not Detected'
                suggestion = "Consider tracking additional cycle information."

            return Response({
                "prediction": prediction_label,
                "suggestion": suggestion
            }, status=status.HTTP_200_OK)

        except ValueError as e:
            # Handle invalid input data (e.g., missing fields, non-numeric input, etc.)
            return Response(
                {"error": f"Error in data submission: {str(e)}"},
                status=status.HTTP_400_BAD_REQUEST
            )

        except Exception as e:
            # Handle unforeseen errors
            return Response(
                {"error": f"Error during prediction: {str(e)}"},
                status=status.HTTP_500_INTERNAL_SERVER_ERROR
            )

