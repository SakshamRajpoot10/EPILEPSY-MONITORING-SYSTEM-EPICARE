from rest_framework import serializers
from .models import Doctor, Patient, Appointment, EpilepsyRecord

class DoctorSerializer(serializers.ModelSerializer):
    class Meta:
        model = Doctor
        fields = '__all__'

class PatientSerializer(serializers.ModelSerializer):
    class Meta:
        model = Patient
        fields = '__all__'

class AppointmentSerializer(serializers.ModelSerializer):
    class Meta:
        model = Appointment
        fields = '__all__'

class EpilepsyRecordSerializer(serializers.ModelSerializer):
    class Meta:
        model = EpilepsyRecord
        fields = '__all__'
