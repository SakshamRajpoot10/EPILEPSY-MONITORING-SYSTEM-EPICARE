from rest_framework.routers import DefaultRouter
from myapp.api_views import DoctorViewSet, PatientViewSet, AppointmentViewSet, EpilepsyRecordViewSet

router = DefaultRouter()
router.register('doctors', DoctorViewSet, basename='doctor')
router.register('patients', PatientViewSet, basename='patient')
router.register('appointments', AppointmentViewSet, basename='appointment')
router.register('epilepsy-cycles', EpilepsyRecordViewSet, basename='menstrual-cycle')

urlpatterns = router.urls
