 
from django.contrib import admin
from django.contrib.auth import views as auth_views
from django.urls import path, include
from django.conf import settings
from django.conf.urls.static import static
from users import views as user_views
from django.contrib import admin
from django.urls import path, include
 
urlpatterns = [
    path('admin/', admin.site.urls),  
    path('', include('users.urls')),  # Users app URLs
    path('', include('myapp.urls')),  # MyApp URLs 
    # API URLs
    path('api/', include('api.api_urls')),  # API entry point    
    
]


if settings.DEBUG:
    urlpatterns += static(settings.MEDIA_URL, document_root=settings.MEDIA_ROOT)


# http://localhost:8000/api/token/ -> Obtain JWT token.
# http://localhost:8000/api/users/ -> User-related API routes.
# http://localhost:8000/api/myapp/ -> App-related API routes.