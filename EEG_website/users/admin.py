from django.contrib import admin
from .models import Profile

class ProfileAdmin(admin.ModelAdmin):
    list_display = ['get_username', 'get_email', 'get_first_name', 'get_last_name']
    list_filter = ['user__is_staff', 'user__is_superuser']
    search_fields = ['user__username', 'user__email', 'user__first_name', 'user__last_name']

    def get_username(self, obj):
        return obj.user.username
    get_username.short_description = 'Username'

    def get_email(self, obj):
        return obj.user.email
    get_email.short_description = 'Email'

    def get_first_name(self, obj):
        return obj.user.first_name
    get_first_name.short_description = 'First Name'

    def get_last_name(self, obj):
        return obj.user.last_name
    get_last_name.short_description = 'Last Name'

admin.site.register(Profile, ProfileAdmin)
