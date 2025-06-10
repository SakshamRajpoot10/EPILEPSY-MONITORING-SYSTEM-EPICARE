Epilepsy Monitoring System – Epicare
📌 Project Overview

Epicare is a wearable, IoT-based real-time epilepsy monitoring system developed using machine learning and sensor fusion to detect epileptic seizures and alert caregivers promptly. It integrates an ECG sensor with an ESP32 microcontroller, an XGBoost ML model, and a mobile/web application for data access, analysis, and alerting. The system achieves 98% accuracy in seizure detection using EEG data from the UCI repository, while also exploring ECG-based detection.

✨ Key Features
•	✅ Real-time seizure detection using XGBoost model
•	✅ Wearable device with ECG sensor + ESP32
•	✅ Web and Android mobile interface
•	✅ Automated emergency alert via SMS and GPS
•	✅ Admin, Doctor, and Patient roles with dashboards
•	✅ Secure data storage in Firebase & integration with cloud
🛠️ Technologies Used

- Programming: Python, JavaScript, Java/Kotlin
- Machine Learning: XGBoost, TensorFlow/Keras, Scikit-learn
- Data Visualization: Matplotlib, Seaborn, MPAndroidChart
- Mobile App: Android Studio, XML Layouts
- Backend: Firebase, Node.js, Express.js
- Microcontroller: ESP32
- Sensors: ECG Sensor (with planned EEG integration)

📐 Architecture Overview

The system architecture includes ECG signal acquisition through a wearable sensor, real-time data processing on ESP32, and transmission to cloud or local server. Seizure detection is done using an XGBoost model trained on EEG data. Alerts are triggered using mobile apps and APIs (e.g., Twilio for SMS). Doctors can access historical records and patient dashboards via web application.

📊 Dataset

- Source: UCI Epileptic Seizure Recognition Dataset
- Records: 11,500 EEG segments, classified into seizure/non-seizure classes
- Features: 178 EEG samples per record (23.6 seconds each)
- Processing: FFT, ApEn, normalization, feature extraction

🤖 Machine Learning Performance

- XGBoost achieved 98% accuracy for binary classification (seizure vs non-seizure)
- Compared Models: SVM (55.39%), Random Forest (73.57%), Naïve Bayes (44.03%)
- Evaluation Metrics: Accuracy, Confusion Matrix, ROC, SHAP

🔌 Hardware Implementation

- ESP32 microcontroller for real-time processing
- ECG sensor module with ADC
- Rechargeable lithium-ion battery
- Wi-Fi/Bluetooth for wireless transmission
- Optional: GPS module for emergency location dispatch

📱 Web & Mobile Features

- User Registration/Login (Admin, Doctor, Patient)
- Real-time seizure monitoring display
- Emergency alert management
- Seizure history log and trends for doctors
- Firebase for authentication and cloud storage

⚙️ Setup Instructions (Summary)

1. Train model using EEG dataset and XGBoost in Python
2. Flash firmware to ESP32 to acquire ECG signals
3. Host Firebase project and configure mobile/web app
4. Connect sensors and test real-time signal acquisition
5. Deploy web app using Node.js backend and React.js frontend

🙌 Acknowledgments

We sincerely thank Ms. Ayushi Sharma, Assistant Professor, HCST, and all CSE faculty members for their mentorship and guidance. Thanks to our teammates Muskan Lalwani, Nandini Agarwal, and Neeraj Pachauri for their contributions throughout the project.

