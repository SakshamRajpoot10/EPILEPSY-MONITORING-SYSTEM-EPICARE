# 🧠 Epilepsy Monitoring System – Epicare

## 📌 **Project Overview**

**Epicare** is a wearable, IoT-based **real-time epilepsy monitoring system** developed using **machine learning** and **sensor fusion** to detect epileptic seizures and instantly alert caregivers. It integrates:

- **ECG sensor**
- **ESP32 microcontroller**
- **XGBoost ML model**
- **Mobile & Web applications**

The system achieves **98% accuracy** using **EEG data from the UCI repository**, and is now being expanded for **ECG-based real-time detection**.

---

## ✨ **Key Features**

- ✅ **Real-time seizure detection** using XGBoost model  
- ✅ **Wearable device** with ECG sensor + ESP32  
- ✅ **Web and Android mobile interface**  
- ✅ **Automated emergency alerts** via SMS and GPS  
- ✅ **Role-based dashboards**: Admin, Doctor, Patient  
- ✅ **Secure cloud storage** with Firebase integration  

---

## 🛠️ **Technologies Used**

- **Programming Languages:** Python, JavaScript, Java/Kotlin  
- **Machine Learning:** XGBoost, TensorFlow/Keras, Scikit-learn  
- **Visualization:** Matplotlib, Seaborn, MPAndroidChart  
- **Mobile App:** Android Studio, XML Layouts  
- **Backend:** Node.js, Express.js, Firebase  
- **Hardware:** ESP32, ECG Sensor (EEG planned)  

---

## 📐 **Architecture Overview**

- **Data Collection:** ECG signals via wearable ECG sensor  
- **Processing:** Onboard with ESP32 microcontroller  
- **Model Inference:** XGBoost trained on EEG dataset  
- **Alert System:** Emergency notifications via SMS (Twilio API), app push notifications  
- **User Roles:** Patient, Doctor, Admin access via web/mobile app  

---

## 📊 **Dataset**

- **Source:** [UCI Epileptic Seizure Recognition Dataset](https://archive.ics.uci.edu/ml/datasets/Epileptic+Seizure+Recognition)
- **Entries:** 11,500 EEG segments  
- **Features:** 178 EEG samples per segment (~23.6s each)  
- **Preprocessing:** FFT, Approximate Entropy (ApEn), normalization, feature extraction  

---

## 🤖 **Machine Learning Performance**

- **Model Used:** XGBoost  
- **Accuracy:** **98%** (binary classification: seizure vs non-seizure)  
- **Comparative Models:**  
  - SVM: 55.39%  
  - Random Forest: 73.57%  
  - Naïve Bayes: 44.03%  
- **Metrics Evaluated:** Accuracy, Confusion Matrix, ROC Curve, SHAP interpretability  

---

## 🔌 **Hardware Implementation**

- 📟 **ESP32 microcontroller** for onboard computation  
- ❤️ **ECG sensor module** with analog-to-digital conversion  
- 🔋 **Rechargeable lithium-ion battery**  
- 🌐 **Wi-Fi/Bluetooth** for wireless communication  
- 📍 **Optional GPS module** for real-time location alerts  

---

## 📱 **Web & Mobile App Features**

- 🔐 **User Authentication** (Admin, Doctor, Patient)
- 📊 **Real-time Seizure Monitoring Dashboard**
- 🚨 **Emergency Alert Management** via mobile and SMS
- 📈 **Seizure History Tracking** and graph visualizations
- ☁️ **Firebase** integration for real-time database & authentication

---

## ⚙️ **Setup Instructions (Summary)**

```bash
1️⃣  Train model using EEG dataset & XGBoost in Python  
2️⃣  Flash ESP32 with firmware to acquire ECG signals  
3️⃣  Setup Firebase project & link with mobile/web apps  
4️⃣  Connect ECG hardware and validate signal acquisition  
5️⃣  Deploy backend (Node.js/Express) & frontend (React.js)
