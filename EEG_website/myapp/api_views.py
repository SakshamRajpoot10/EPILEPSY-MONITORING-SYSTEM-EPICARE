from rest_framework import viewsets, permissions

from myapp.views import process_sensor_data
from .models import Doctor, Patient, Appointment, EpilepsyRecord
from .serializers import DoctorSerializer, PatientSerializer, AppointmentSerializer, EpilepsyRecordSerializer
 
from django.http import JsonResponse
import json
from django.contrib.auth.decorators import login_required
from django.core.exceptions import PermissionDenied
from django.views.decorators.csrf import csrf_exempt
from rest_framework.views import APIView
from rest_framework.permissions import IsAuthenticated
from rest_framework.authentication import TokenAuthentication
 
class DoctorViewSet(viewsets.ModelViewSet):
    queryset = Doctor.objects.all()
    serializer_class = DoctorSerializer
    permission_classes = [permissions.IsAuthenticated]

class PatientViewSet(viewsets.ModelViewSet):
    queryset = Patient.objects.all()
    serializer_class = PatientSerializer
    permission_classes = [permissions.IsAuthenticated]

class AppointmentViewSet(viewsets.ModelViewSet):
    queryset = Appointment.objects.all()
    serializer_class = AppointmentSerializer
    permission_classes = [permissions.IsAuthenticated]

class EpilepsyRecordViewSet(viewsets.ModelViewSet):
    queryset = EpilepsyRecord.objects.all()
    serializer_class = EpilepsyRecordSerializer
    permission_classes = [permissions.IsAuthenticated]

class PredictDataApiView(APIView):
    authentication_classes = [TokenAuthentication]
    permission_classes = [IsAuthenticated]

    def post(self, request):
        try:
            # Process the data from the request body
            sensor_data = request.data.get('sensor_data', [])
            if not sensor_data:
                return JsonResponse({'error': 'No sensor data provided.'}, status=400)

            # Process sensor data to get the prediction (assuming a function for that)
            prediction_label, suggestion = process_sensor_data(sensor_data)

            return JsonResponse({
                'prediction': prediction_label,
                'suggestion': suggestion
            })
        except Exception as e:
            return JsonResponse({'error': str(e)}, status=500)
