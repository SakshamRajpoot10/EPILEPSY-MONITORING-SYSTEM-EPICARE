from django.urls import path, include
from rest_framework.routers import DefaultRouter
from rest_framework_simplejwt.views import TokenObtainPairView, TokenRefreshView
from myapp.api_views import PredictDataApiView
 
from users.api_views  import  RegisterAPI, LoginAPI, ChangePasswordAPI, forgot_password, UpdateProfileAPI

router = DefaultRouter()
urlpatterns = [
    # JWT Authentication URLs
    path('token/', TokenObtainPairView.as_view(), name='token_obtain_pair'),
    path('token/refresh/', TokenRefreshView.as_view(), name='token_refresh'),
    path('register/', RegisterAPI.as_view(), name='api_register'),
    path('login/', LoginAPI.as_view(), name='api-login'),
    path('change-password/', ChangePasswordAPI.as_view(), name='api_change_password'),
    path('forgot-password/', forgot_password, name='api_forgot_password'),
    path('update-profile/', UpdateProfileAPI.as_view(), name='api_update_profile'),
    path('pred', PredictDataApiView.as_view() , name='api_pred'), 
    path('', include('myapp.api_urls')),
    path('', include(router.urls)),
   
]
 