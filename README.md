# 🚌 Bus Tracker - User Panel Android App

A modern Android application built with Jetpack Compose and Material 3 design for bus passengers to track live buses, view ETAs, and manage their journey.

## ✨ Features

### 🔐 Authentication
- **Login Screen**: Email/Phone + Password authentication
- **Register Screen**: Complete user registration with Name, Email, Phone, and Password
- **Secure Session Management**: Automatic login state persistence

### 🗺️ Interactive Map (Mapbox Integration)
- **Real-time Bus Tracking**: View live bus locations with custom 🚍 markers
- **User Location**: Blue dot showing current user location
- **Bus Details**: Tap bus markers to see detailed information
- **Map Controls**: Refresh button and location centering FAB

### 📍 Bus Information
- **Live Bus Data**: Real-time bus locations and ETAs
- **Detailed Bus Cards**: Shows Bus ID, ETA, Speed, Route, and Last Updated
- **Track Bus Feature**: Lock map camera to specific bus for tracking

### 🚏 Stops & ETA Management
- **Bus Stop List**: Comprehensive list of all bus stops
- **ETA Information**: Real-time arrival times for each bus at each stop
- **Route Details**: Bus route information for each stop

### 👤 User Profile
- **User Information**: Display Name, Email, Phone, and User ID
- **Profile Management**: Clean Material 3 design with user avatar
- **Logout Functionality**: Secure session termination

### ⚙️ Settings & Customization
- **Multi-language Support**: English, Hindi, and Gujarati
- **App Information**: Version details and build information
- **About Section**: App description and features overview

### 🔒 Permission Management
- **Custom Permission Dialog**: User-friendly location permission request
- **Location Services**: Integrated with Google Play Services
- **Permission Handling**: Proper Android permission management

## 🏗️ Architecture & Technology Stack

### **Frontend**
- **Jetpack Compose**: Modern declarative UI toolkit
- **Material 3**: Latest Material Design components
- **Navigation Rail**: Side navigation for larger screens

### **Backend & Data**
- **MVVM Architecture**: Clean separation of concerns
- **StateFlow**: Reactive state management
- **Coroutines**: Asynchronous programming
- **Dummy Data Repository**: Ready for real API integration

### **Maps & Location**
- **Mapbox Maps**: High-performance mapping solution
- **Google Play Services**: Location and permission handling
- **Custom Markers**: Bus and user location indicators

### **Dependencies**
```kotlin
// Core
implementation("androidx.core:core-ktx:1.17.0")
implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.9.3")
implementation("androidx.activity:activity-compose:1.10.1")

// Compose
implementation("androidx.compose:compose-bom:2024.09.00")
implementation("androidx.compose.material3:material3")
implementation("androidx.navigation:navigation-compose:2.7.7")

// Maps & Location
implementation("com.mapbox.maps:android:10.16.5")
implementation("com.google.android.gms:play-services-location:21.2.0")

// ViewModel & Coroutines
implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.0")
```

## 🚀 Getting Started

### Prerequisites
- Android Studio Hedgehog or later
- Android SDK 24+ (API Level 24)
- Kotlin 2.0.21+
- Mapbox Access Token (included in the project)

### Installation
1. Clone the repository
2. Open in Android Studio
3. Sync Gradle files
4. Build and run on device/emulator

### Configuration
The app is pre-configured with:
- Mapbox access token in `AndroidManifest.xml`
- Dummy data for buses and stops
- Location permissions setup
- Material 3 theme configuration

## 📱 Screenshots & UI

### Main Features
- **Navigation Rail**: Left-side navigation with app sections
- **Map Screen**: Full-screen Mapbox integration with bus markers
- **Responsive Design**: Adapts to different screen sizes
- **Material 3**: Modern design with dynamic color support

### Color Scheme
- **Primary**: Blue (#2196F3)
- **Secondary**: Green (#4CAF50)
- **Surface**: Light/Dark theme support
- **Bus Markers**: Orange (#FF5722)
- **User Location**: Blue (#2196F3)

## 🔧 Customization

### Adding Real Data
1. Replace `DummyDataRepository` with real API calls
2. Update data models as needed
3. Implement real-time data streaming
4. Add authentication backend

### Map Customization
1. Modify Mapbox style in `HomeScreen`
2. Add custom map layers
3. Implement route visualization
4. Add traffic data integration

### Language Support
1. Add more languages to `AppLanguage` enum
2. Implement string resource localization
3. Add RTL language support
4. Dynamic language switching

## 📊 Data Models

### Core Entities
```kotlin
data class User(
    val id: String,
    val name: String,
    val email: String,
    val phone: String
)

data class Bus(
    val id: String,
    val latitude: Double,
    val longitude: Double,
    val eta: Int,
    val speed: Double,
    val lastUpdated: String,
    val route: String
)

data class BusStop(
    val id: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val buses: List<BusETA>
)
```

## 🧪 Testing

### Unit Tests
- ViewModel testing with dummy data
- Repository pattern testing
- Data model validation

### UI Tests
- Compose UI testing
- Navigation testing
- Permission flow testing

## 📈 Future Enhancements

### Planned Features
- **Real-time Updates**: WebSocket integration for live data
- **Push Notifications**: Bus arrival alerts
- **Offline Support**: Cached data for offline viewing
- **Route Planning**: Multi-stop journey planning
- **Favorites**: Save frequently used routes

### Technical Improvements
- **Database Integration**: Room database for local storage
- **API Integration**: RESTful API with Retrofit
- **Image Caching**: Coil for efficient image loading
- **Analytics**: User behavior tracking
- **Crash Reporting**: Firebase Crashlytics integration

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Implement your changes
4. Add tests and documentation
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🙏 Acknowledgments

- **Mapbox**: For providing excellent mapping services
- **Google**: For Android development tools and Play Services
- **JetBrains**: For Kotlin and Compose Multiplatform
- **Material Design**: For design system and components

---

