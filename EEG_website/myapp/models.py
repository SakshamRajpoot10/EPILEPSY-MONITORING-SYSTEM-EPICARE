#myapp apps
from django.db import models
from django.urls import reverse
from django.utils import timezone
from django.contrib.auth.models import User

class EpilepsyRecord(models.Model):
    user = models.ForeignKey(User, on_delete=models.CASCADE)
    recordno = models.CharField(max_length=10, unique=True, blank=True)  # Store as CharField for 'R0001' format
    patient_id = models.IntegerField()  # Assuming patient ID is a numeric identifier
    json_data = models.JSONField()  # Stores structured JSON data
    predicted_class = models.IntegerField()  # Assuming classification result is an integer
    actual_class = models.IntegerField()  # Ground truth class as an integer    
    # New field
    Actual_class_CHOICES = [
        (0, 'No'),
        (1, 'Yes'),
       
    ]
    predicted_class = models.IntegerField(choices=Actual_class_CHOICES, default=0)
    actual_class = models.IntegerField(choices=Actual_class_CHOICES, default=0)
    created_at = models.DateTimeField(auto_now_add=True)  # Timestamp for record creation
    
    
    def get_absolute_url(self):
            return reverse('epilepsy_detail', kwargs={'pk': self.pk})

    def save(self, *args, **kwargs):
        if not self.recordno:
            # Generate a new record number with format 'R0001', 'R0002', etc.
            last_record = EpilepsyRecord.objects.filter(user=self.user).order_by('id').last()
            if last_record:
                last_record_number = int(last_record.recordno[1:])  # Strip the 'R' and get the number
                new_record_number = last_record_number + 1
            else:
                new_record_number = 1  # Start with 'R0001' if no previous records
            self.recordno = f'R{new_record_number:04}'  # Format as 'R0001'
        
        super().save(*args, **kwargs)  # Call the original save method

    def __str__(self):
        return f"Cycle on {self.created_at.strftime('%Y-%m-%d')} for {self.user.username}"

 
    

departments=[('Neology', 'Neology'),
             ('Cardiologist','Cardiologist'),
('Dermatologists','Dermatologists'),
('Emergency Medicine Specialists','Emergency Medicine Specialists'),
('Allergists/Immunologists','Allergists/Immunologists'),
('Anesthesiologists','Anesthesiologists'),
('Colon and Rectal Surgeons','Colon and Rectal Surgeons')
]
class Doctor(models.Model):
    user=models.OneToOneField(User,on_delete=models.CASCADE)
    profile_pic= models.ImageField(upload_to='profile_pic/DoctorProfilePic/',null=True,blank=True)
    address = models.CharField(max_length=40)
    mobile = models.CharField(max_length=20,null=True)
    department= models.CharField(max_length=50,choices=departments,default='Cardiologist')
    status=models.BooleanField(default=False)

    @property
    def get_name(self):
        return self.user.first_name+" "+self.user.last_name
    @property
    def get_id(self):
        return self.user.id
    def __str__(self):
        # return "{} ({})".format(self.user.first_name,self.department)
        return self.user.first_name+" "+self.user.last_name
 
class Patient(models.Model):
    user=models.OneToOneField(User,on_delete=models.CASCADE)
    profile_pic= models.ImageField(upload_to='profile_pic/PatientProfilePic/',null=True,blank=True)
    address = models.CharField(max_length=40)
    mobile = models.CharField(max_length=20,null=False)
    symptoms = models.CharField(max_length=100,null=False)
    assignedDoctorId = models.PositiveIntegerField(null=True)
    admitDate=models.DateField(auto_now=True)
    status=models.BooleanField(default=False)
    @property
    def get_name(self):
        return self.user.first_name+" "+self.user.last_name
    @property
    def get_id(self):
        return self.user.id
    def __str__(self):
        return self.user.first_name+" ("+self.symptoms+")"

# New model to manage the relationship between Doctors and Patients
class DoctorPatient(models.Model):
    doctor = models.ForeignKey(Doctor, on_delete=models.CASCADE)
    patient = models.ForeignKey(Patient, on_delete=models.CASCADE)
    assigned_date = models.DateTimeField(auto_now_add=True)  # Track when they were assigned

    class Meta:
        unique_together = ('doctor', 'patient')  # Prevent duplicate assignments

    def __str__(self):
        return f"Dr. {self.doctor.get_name} -> {self.patient.get_name}"
    
class Appointment(models.Model):
    patientId=models.PositiveIntegerField(null=True)
    doctorId=models.PositiveIntegerField(null=True)
    patientName=models.CharField(max_length=40,null=True)
    doctorName=models.CharField(max_length=40,null=True)
    appointmentDate=models.DateField(auto_now=True)
    description=models.TextField(max_length=500)
    status=models.BooleanField(default=False)

class PatientDischargeDetails(models.Model):
    patientId=models.PositiveIntegerField(null=True)
    patientName=models.CharField(max_length=40)
    assignedDoctorName=models.CharField(max_length=40)
    address = models.CharField(max_length=40)
    mobile = models.CharField(max_length=20,null=True)
    symptoms = models.CharField(max_length=100,null=True)

    admitDate=models.DateField(null=False)
    releaseDate=models.DateField(null=False)
    daySpent=models.PositiveIntegerField(null=False)

    roomCharge=models.PositiveIntegerField(null=False)
    medicineCost=models.PositiveIntegerField(null=False)
    doctorFee=models.PositiveIntegerField(null=False)
    OtherCharge=models.PositiveIntegerField(null=False)
    total=models.PositiveIntegerField(null=False)


