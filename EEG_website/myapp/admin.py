from django.contrib import admin
from .models import DoctorPatient, EpilepsyRecord

admin.site.site_header = "Super Admin Panel "
admin.site.site_title = "Epicare Admin Portal"
admin.site.index_title = "Welcome to Epicare Portal"
from .models import Doctor,Patient,Appointment,PatientDischargeDetails

class EpilepsyRecordAdmin(admin.ModelAdmin):
    # Display the following fields in the admin list view
    list_display = (
        'get_user_username',  
        'recordno',
        'patient_id',
        'predicted_class',
        'actual_class',
        'created_at',        
    )
    
    # Add search functionality for certain fields
    search_fields = ('user__username', 'patient_id', 'recordno')
    
    # Filter options based on these fields
    list_filter = ('user__username', 'created_at', 'patient_id')
    
    # Custom method to display the user's username
    def get_user_username(self, obj):
        return obj.user.username if obj.user else None
    get_user_username.short_description = 'Username'

# Register the EpilepsyRecord model with the custom admin options
admin.site.register(EpilepsyRecord, EpilepsyRecordAdmin)
 
class DoctorAdmin(admin.ModelAdmin):
    # Display specific fields in the admin list view
    list_display = ('get_full_name', 'department', 'address', 'mobile', 'status')

    # Add filtering capabilities
    list_filter = ('department', 'status')

    # Enable search functionality
    search_fields = ('user__first_name', 'user__last_name', 'mobile', 'department', 'address')

    # Add ordering
    ordering = ('user__first_name',)

    # Custom method to display the full name in admin
    def get_full_name(self, obj):
        return f"{obj.user.first_name} {obj.user.last_name}"
    
    get_full_name.admin_order_field = 'user__first_name'  # Allows sorting by first name
    get_full_name.short_description = 'Doctor Name'  # Custom column name in admin

# Register the model with the admin site
admin.site.register(Doctor, DoctorAdmin)
 

class PatientAdmin(admin.ModelAdmin):
    # Display specific fields in the admin list view
    list_display = ('user', 'mobile', 'symptoms', 'assignedDoctorId', 'admitDate', 'status')
    # Add filtering options
    list_filter = ('status', 'admitDate')
    # Enable search functionality
    search_fields = ('user__first_name', 'user__last_name', 'mobile', 'symptoms', 'address')
    # Add ordering
    ordering = ('admitDate',)

class DoctorPatientAdmin(admin.ModelAdmin):
    list_display = ('doctor_name', 'patient_name', 'assigned_date')  # Columns to display
    list_filter = ('doctor__department',)  # Filter by doctor’s department
    search_fields = ('doctor__user__first_name', 'doctor__user__last_name', 'patient__user__first_name', 'patient__user__last_name')  # Search by doctor or patient name
    ordering = ('-assigned_date',)  # Show most recent assignments first

    def doctor_name(self, obj):
        return f"{obj.doctor.user.first_name} {obj.doctor.user.last_name}"
    doctor_name.admin_order_field = 'doctor__user__first_name'  # Enable sorting
    doctor_name.short_description = 'Doctor'

    def patient_name(self, obj):
        return f"{obj.patient.user.first_name} {obj.patient.user.last_name}"
    patient_name.admin_order_field = 'patient__user__first_name'  # Enable sorting
    patient_name.short_description = 'Patient'

# Register the model in the admin panel
admin.site.register(DoctorPatient, DoctorPatientAdmin)

# Register the model with the enhanced admin configuration
admin.site.register(Patient, PatientAdmin)

# class AppointmentAdmin(admin.ModelAdmin):
#     list_display = ('patient', 'doctor', 'appointment_date', 'appointment_time', 'status')
#     list_filter = ('status', 'appointment_date')
#     search_fields = ('patient__user__first_name', 'patient__user__last_name',
#                      'doctor__user__first_name', 'doctor__user__last_name')
#     ordering = ('-appointment_date', 'appointment_time')
#     list_editable = ('status',)

# # Register the model with the enhanced admin configuration
# admin.site.register(Appointment, AppointmentAdmin)

class AppointmentAdmin(admin.ModelAdmin):
    pass
admin.site.register(Appointment, AppointmentAdmin)

class PatientDischargeDetailsAdmin(admin.ModelAdmin):
    pass
admin.site.register(PatientDischargeDetails, PatientDischargeDetailsAdmin)