from django.urls import path
from . import views
from .views import (
    add_doctor_to_patient,
    doctor_list,
    mypatient_list,
    first_adds,
    gov_help,
    learn_experts,
    precaution_helps,
    search_events,
    UserEpilepsyRecordListView,
    EpilepsyRecordDetailView,
    EpilepsyRecordCreateView,
    EpilepsyRecordUpdateView,
    EpilepsyRecordDeleteView,
    epilepsy_history,
    user_doctor_list,
    view_epilepsy_history,
    search,
    about,
    contact,
    predict_data,
    logout_user,
)


urlpatterns = [
    # Home and Prediction
    path('', views.home, name='home'),
    path('home/', UserEpilepsyRecordListView.as_view(), name='myapp-home'),
    path('predict/', predict_data, name='predict_data'),

    # epilepsy History
    path('epilepsy-history/', epilepsy_history, name='epilepsy_history'),
    path('epilepsy-history/view/', view_epilepsy_history, name='view_epilepsy_history'),

    # User-specific epilepsy Cycles
    path('user/<str:username>/', UserEpilepsyRecordListView.as_view(), name='user-epilepsys'),
    path('add-doctor/<int:doctor_id>/', add_doctor_to_patient, name='add_doctor'),
    path('doctors/<str:username>/', user_doctor_list, name='mydoctor-list'),
    path('mypatient/<str:username>/', mypatient_list, name='mypatient-list'),
    

    # epilepsy Cycle CRUD Operations
    path('epilepsy/<int:pk>/', EpilepsyRecordDetailView.as_view(), name='epilepsy_detail'),
    path('epilepsy/new/', EpilepsyRecordCreateView.as_view(), name='epilepsy-create'),
    path('epilepsy/<int:pk>/update/', EpilepsyRecordUpdateView.as_view(), name='epilepsy-update'),
    path('epilepsy/<int:pk>/delete/', EpilepsyRecordDeleteView.as_view(), name='epilepsy-delete'),

    # Search
    path('search/', search, name='search'),
    path('search-events/', search_events, name='search_events'),

    # Static Pages
    path('about/', about, name='myapp-about'),
    path('contact/', contact, name='myapp-contact'),
    path('learn-experts/', learn_experts, name='myapp-learn-experts'),
    path('first-adds/', first_adds, name='myapp-first-adds'),
    path('gov-help/', gov_help, name='myapp-gov-helps'),
    path('doctors/', doctor_list, name='myapp-doctor-list'),
    path('precaution-helps/', precaution_helps, name='myapp-precaution-helps'),    
    # Logout
    path('logout/', logout_user, name='logout'),
]

