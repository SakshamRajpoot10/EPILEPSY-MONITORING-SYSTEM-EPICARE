from django.conf import settings
from django.shortcuts import render, get_object_or_404, redirect
from django.http import JsonResponse, HttpResponse, FileResponse, HttpResponseServerError
from django.contrib.auth import authenticate, login, logout
from django.contrib.auth.decorators import login_required
from django.contrib.auth.mixins import LoginRequiredMixin, UserPassesTestMixin
from django.contrib.auth.models import User
from django.urls import reverse_lazy
from django.utils import timezone
from django.views.generic import ListView, DetailView, CreateView, UpdateView, DeleteView
from django.contrib.staticfiles.views import serve
from django.contrib import messages
from django.db.models import Q
from datetime import datetime

import os
import joblib 
import json
import numpy as np
import pickle
from django.views.decorators.csrf import csrf_exempt
from .models import Doctor, DoctorPatient,  EpilepsyRecord, Patient
from .forms import EpilepsyRecordForm

module_dir = os.path.dirname(__file__)
try:
    model_path = os.path.join(settings.BASE_DIR, 'model/XgBoostModel.pickle')
    model = joblib.load(model_path)
    print("Model loaded")
except FileNotFoundError:
    # Handle the case where the model file does not exist
    def predict_data(request):
        return HttpResponseServerError("Model file not found. Please check the model path.")
except Exception as e:
    # Handle any other exceptions related to loading the model
    def predict_data(request):
        return HttpResponseServerError(f"An error occurred while loading the model: {e}")
 
def process_sensor_data(sensor_data):
    try:
        # Convert the sensor_data from JSON string to Python list
        sensor_data = json.loads(sensor_data)

        # Ensure that the sensor_data is a list of numeric values
        if not isinstance(sensor_data, list) or not all(isinstance(i, (int, float)) for i in sensor_data):
            raise ValueError("Sensor data must be a list of numbers.")
        
        # If the input data is smaller than 178, pad it with zeros (or another appropriate value)
        expected_length = 178
        current_length = len(sensor_data)

        if current_length < expected_length:
            # Padding with zeros to match the expected feature size (you could use a different method if needed)
            sensor_data = sensor_data + [0] * (expected_length - current_length)
        
        # Convert the sensor_data list to a NumPy array and reshape it for prediction
        input_data = np.array(sensor_data).reshape(1, -1)  # Reshaping to match model input format

        # Make the prediction
        prediction = model.predict(input_data)

        # Set prediction label based on prediction value
        if prediction[0] == 1:
            prediction_label = 'Epilepsy Detected'
            suggestion = "Your epilepsy cycle pattern suggests optimal care periods."
        else:
            prediction_label = 'Epilepsy Not Detected'
            suggestion = "Consider tracking additional cycle information."
        
        return prediction_label, suggestion

    except ValueError as e:
        # Handle invalid input data (e.g., missing fields, non-numeric input, etc.)
        return f"Error in data submission: {str(e)}", "Please check your input values."
    
    except Exception as e:
        # Handle other unforeseen errors
        return f"Error during prediction: {str(e)}", "Please try again later or contact support."

# API view function for prediction
 
# @csrf_exempt
# def predict_data_api(request):
#     if request.method == 'POST':
#         try:
#             data = json.loads(request.body)
#             sensor_data = data.get('sensor_data', [])

#             if not sensor_data:
#                 return JsonResponse({'error': 'No sensor data provided.'}, status=400)

#             # Process the sensor data and get the prediction (assuming you have a method for this)
#             prediction_label, suggestion = process_sensor_data(sensor_data)

#             return JsonResponse({
#                 'prediction': prediction_label,
#                 'suggestion': suggestion
#             })

#         except json.JSONDecodeError:
#             return JsonResponse({'error': 'Invalid JSON'}, status=400)
    
#     return JsonResponse({'error': 'Invalid method. Only POST requests are allowed.'}, status=405)

 
# Standard view function for prediction
@login_required 
def predict_data(request):
    if request.method == 'POST':
        sensor_data = request.POST.get('sensor_data')  # This should match your input name in the HTML form

        # Check if sensor_data is provided
        if not sensor_data:
            return render(request, 'myapp/pred_data.html', {'prediction': 'No sensor data provided.', 'suggestion': 'Please check your input values.'})

        # Process sensor data and get prediction
        prediction_label, suggestion = process_sensor_data(sensor_data)

        # Send data back to template
        context = {
            'prediction': prediction_label,
            'suggestion': suggestion,
        }
        return render(request, 'myapp/pred_data.html', context)

    # In case of GET request, simply render the empty form
    return render(request, 'myapp/pred_data.html')

@login_required
def epilepsy_history(request):
    if request.method == 'POST':
        form = EpilepsyRecordForm(request.POST)
        if form.is_valid():
            epilepsy_cycle = form.save(commit=False)
            epilepsy_cycle.user = request.user
            epilepsy_cycle.save()
            return redirect('view_epilepsy_history')
    else:
        form = EpilepsyRecordForm()

    return render(request, 'epilepsy_cycle_form.html', {'form': form})

@login_required
def view_epilepsy_history(request):
    history = EpilepsyRecord.objects.filter(user=request.user).order_by('-created_at')
    return render(request, 'myapp/epilepsy_cycle_history.html', {'history': history})


def search_events(request):
    if request.method == 'GET':
        start_date_str = request.GET.get('start_date', '')
        end_date_str = request.GET.get('end_date', '')
        recordno = request.GET.get('recordno', '')
        status = request.GET.get('status', '')

        # Initialize queryset
        epilepsy_records = EpilepsyRecord.objects.filter(user=request.user).order_by('-created_at')
        

        # Parse and filter by start date
        if start_date_str:
            try:
                start_date = datetime.strptime(start_date_str, "%Y-%m-%d")
                epilepsy_records = epilepsy_records.filter(created_at__gte=start_date)
            except ValueError:
                pass  # Ignore invalid date

        # Parse and filter by end date
        if end_date_str:
            try:
                end_date = datetime.strptime(end_date_str, "%Y-%m-%d")
                epilepsy_records = epilepsy_records.filter(created_at__lte=end_date)
            except ValueError:
                pass  # Ignore invalid date

        # Filter by record number if provided
        if recordno:
            epilepsy_records = epilepsy_records.filter(recordno=recordno)

        # Filter by predicted class (assuming "status" is referring to predicted_class)
        if status:
            try:
                status_value = int(status)
                epilepsy_records = epilepsy_records.filter(predicted_class=status_value)
            except ValueError:
                pass  # Ignore invalid input

        # Return filtered results
        return render(request, 'myapp/search_events.html', {'epilepsy_records': epilepsy_records})

    # Default empty context for non-GET requests
    return render(request, 'myapp/search_events.html', {'epilepsy_records': None})
def home(request):
 
    return render(request, 'myapp/home.html')

def search(request):
    template = 'myapp/home.html'
    query = request.GET.get('q')

    if query:
        result = EpilepsyRecord.objects.filter(
            Q(user__username__icontains=query) | Q(recordno__icontains=query)
        )
    else:
        result = EpilepsyRecord.objects.none()

    context = {'epilepsy_cycles': result}
    return render(request, template, context)

class UserEpilepsyRecordListView(ListView):
    model = EpilepsyRecord
    template_name = 'myapp/user_epilepsy_cycles.html'
    context_object_name = 'epilepsy_cycles'
    paginate_by = 5

    def get_queryset(self):
        user = self.request.user  # ✅ Use logged-in user instead of URL parameter
        return EpilepsyRecord.objects.filter(user=user).order_by('-created_at')


 
class EpilepsyRecordDetailView(DetailView):
    model = EpilepsyRecord
    template_name = 'myapp/epilepsy_cycle_detail.html'

class EpilepsyRecordCreateView(LoginRequiredMixin, CreateView):
    model = EpilepsyRecord
    template_name = 'myapp/epilepsy_cycle_form.html'
    form_class = EpilepsyRecordForm

    def form_valid(self, form):
        form.instance.user = self.request.user
        messages.success(self.request, 'epilepsy cycle successfully created!')
        return super().form_valid(form)

    def form_invalid(self, form):
        messages.error(self.request, 'There was an error in your form. Please check your input and try again.')
        return super().form_invalid(form)

    # Optional: if you want to redirect after form submission, set success_url
    def get_success_url(self):
        return reverse_lazy('epilepsy-create') 

 
    

class EpilepsyRecordUpdateView(LoginRequiredMixin, UserPassesTestMixin, UpdateView):
    model = EpilepsyRecord
    template_name = 'myapp/epilepsy_cycle_form.html'
    form_class = EpilepsyRecordForm

    def form_valid(self, form):
        form.instance.user = self.request.user
        return super().form_valid(form)

    def test_func(self):
        epilepsy_cycle = self.get_object()
        return self.request.user == epilepsy_cycle.user

class EpilepsyRecordDeleteView(LoginRequiredMixin, UserPassesTestMixin, DeleteView):
    model = EpilepsyRecord
    success_url = '/'
    template_name = 'myapp/epilepsy_cycle_confirm_delete.html'

    def test_func(self):
        epilepsy_cycle = self.get_object()
        return self.request.user == epilepsy_cycle.user

def about(request):
    return render(request, 'myapp/about.html', {'title': 'About'})

def contact(request):
    return render(request, 'myapp/contact.html', {'title': 'Contact Us'})

def learn_experts(request):
    return render(request, 'myapp/learn-experts.html', {'title': 'learn experts'})

def first_adds(request):
    return render(request, 'myapp/first-adds.html', {'title': 'First-Adds'})

def gov_help(request):
    return render(request, 'myapp/gov-helps.html', {'title': 'Gov. Helps'})

def precaution_helps(request):
    return render(request, 'myapp/precaution-helps.html', {'title': 'Gov. Helps'})


def doctor_list(request):
    doctors = Doctor.objects.all()

    if request.user.is_authenticated:  # Check if user is logged in
        try:
            user_patient = Patient.objects.get(user=request.user)  # Get logged-in patient's profile
            assigned_doctors = DoctorPatient.objects.filter(patient=user_patient).values_list('doctor_id', flat=True)  # Get assigned doctor IDs

            for doctor in doctors:
                doctor.is_assigned = doctor.id in assigned_doctors  # Mark assigned doctors
        except Patient.DoesNotExist:
            # If logged-in user is not a patient, treat them as a guest
            for doctor in doctors:
                doctor.is_assigned = False
    else:
        # If guest, do not mark any doctors as assigned
        for doctor in doctors:
            doctor.is_assigned = False

    return render(request, 'myapp/doctor-list.html', {'doctors': doctors})

def user_doctor_list(request, username):
    user = get_object_or_404(User, username=username)

    # Check if the user has a corresponding patient profile
    patient = Patient.objects.filter(user=user).first()  # ✅ Use `.first()` to avoid 404

    if not patient:
        # ✅ Redirect or show an empty doctor list if the user is not a patient
        return render(request, 'myapp/mydoctor-list.html', {
            'title': 'My Doctors',
            'doctors': [],
            'error_message': "This user is not registered as a patient."
        })

    # Get all doctors assigned to the patient
    assigned_doctors = Doctor.objects.filter(doctorpatient__patient=patient)

    return render(request, 'myapp/mydoctor-list.html', {
        'title': 'My Doctors',
        'doctors': assigned_doctors
    })

 


@login_required
def mypatient_list(request, username):
    user = get_object_or_404(User, username=username)

    # Ensure the user is a doctor
    doctor = get_object_or_404(Doctor, user=user)

    # Get all patients assigned to the doctor
    assigned_patients = Patient.objects.filter(doctorpatient__doctor=doctor)

    return render(request, 'myapp/mypatient-list.html', {
        'title': 'My Patients',
        'patients': assigned_patients
    })

@login_required
def add_doctor_to_patient(request, doctor_id):
    patient = get_object_or_404(Patient, user=request.user)  # Get logged-in patient
    doctor = get_object_or_404(Doctor, id=doctor_id)  # Get doctor by ID

    if DoctorPatient.objects.filter(doctor=doctor, patient=patient).exists():
        return JsonResponse({'success': False, 'message': 'You have already added this doctor.'})

    DoctorPatient.objects.create(doctor=doctor, patient=patient)
    return JsonResponse({'success': True, 'message': 'Doctor added successfully!'})

def logout_user(request):
    logout(request)
    return redirect('login')