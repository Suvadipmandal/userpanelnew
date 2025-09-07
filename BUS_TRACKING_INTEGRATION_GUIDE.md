# Bus Tracking Integration Guide

This guide provides complete setup instructions for the Android Jetpack Compose + Material3 bus tracking screen with Mapbox Maps SDK and Firebase Realtime Database.

## 🚀 Features Implemented

- **Material3 UI**: Modern, beautiful interface with proper theming
- **Mapbox Integration**: Real-time map with custom markers and polylines
- **Firebase Realtime Database**: Live bus location updates every 2 seconds
- **Location Permissions**: Runtime permission handling for user location
- **Demo Bus Simulator**: Automatic bus movement simulation around Vadodara Junction
- **Real-time Tracking**: Live ETA, distance, and route information
- **Error Handling**: Comprehensive error handling for all edge cases

## 📋 Prerequisites

1. **Android Studio** (latest version)
2. **Firebase Project** with Realtime Database enabled
3. **Mapbox Account** with access token
4. **Android Device/Emulator** with location services enabled

## 🔧 Setup Instructions

### 1. Firebase Setup

#### Step 1: Create Firebase Project
1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Create a new project or use existing one
3. Enable **Realtime Database**
4. Set database rules to allow read/write (for development):

```json
{
  "rules": {
    "buses": {
      ".read": true,
      ".write": true
    },
    "tracking_sessions": {
      ".read": true,
      ".write": true
    }
  }
}
```

#### Step 2: Add Firebase to Android Project
1. Download `google-services.json` from Firebase Console
2. Place it in `app/` directory
3. The project already has Firebase dependencies configured

### 2. Mapbox Setup

#### Step 1: Get Mapbox Access Token
1. Go to [Mapbox Account](https://account.mapbox.com/)
2. Create access token with appropriate scopes
3. Add token to your project (already configured in existing code)

### 3. Gradle Dependencies

The project already includes all necessary dependencies in `app/build.gradle.kts`:

```kotlin
dependencies {
    // Core dependencies
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    
    // Navigation
    implementation(libs.androidx.navigation.compose)
    
    // Location services
    implementation(libs.google.play.services.location)
    
    // Mapbox
    implementation(libs.mapbox.maps)
    
    // ViewModel and LiveData
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    
    // Coroutines
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.play.services)
    
    // Permissions
    implementation(libs.accompanist.permissions)
    
    // Firebase
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.perf)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)
    implementation(libs.firebase.firestore)
}
```

### 4. Android Manifest Permissions

Add these permissions to `app/src/main/AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

## 🎯 Usage Instructions

### 1. Basic Integration

To use the BusTrackingScreen in your app:

```kotlin
@Composable
fun YourMainScreen() {
    BusTrackingScreen()
}
```

### 2. Custom Integration

For more control over the tracking:

```kotlin
@Composable
fun CustomBusTrackingScreen() {
    val viewModel: BusTrackingViewModel = viewModel()
    
    // Access tracking state
    val isTracking by viewModel.isTracking.collectAsState()
    val busLocation by viewModel.busLocation.collectAsState()
    val userLocation by viewModel.userLocation.collectAsState()
    
    BusTrackingScreen(
        viewModel = viewModel
    )
}
```

### 3. Manual Tracking Control

```kotlin
// Start tracking
viewModel.startTracking("demoBus", "user123")

// Stop tracking
viewModel.stopTracking()

// Check if tracking
val isTracking = viewModel.isTracking.value
```

## 🔄 How It Works

### 1. Initialization Flow
1. App starts and initializes `BusTrackingViewModel`
2. Firebase demo data is set up automatically
3. Bus simulator starts writing coordinates every 2 seconds
4. Map centers on Vadodara Junction (22.3072, 73.1812)

### 2. Tracking Flow
1. User clicks "Start Tracking" button
2. App requests location permission (if not granted)
3. Map navigates to Vadodara Junction
4. Firebase listener starts receiving bus location updates
5. Bus marker moves smoothly on map
6. Polyline draws from user location to bus
7. ETA and distance update in real-time

### 3. Demo Bus Simulation
- Bus follows predefined route around Vadodara Junction
- Coordinates update every 2 seconds in Firebase
- Realistic speed, heading, and passenger data
- Route includes 25+ waypoints for smooth movement

## 📱 UI Components

### 1. Main Screen Elements
- **Mapbox MapView**: Full-screen map with custom styling
- **Top App Bar**: Shows current tracking status
- **Floating Action Button**: Start/Stop tracking toggle
- **Live Status Card**: Shows ETA, distance, and bus info
- **Error Messages**: User-friendly error handling

### 2. Material3 Design
- **Colors**: Custom color scheme for tracking states
- **Typography**: Material3 typography scale
- **Shapes**: Rounded corners and proper elevation
- **Animations**: Smooth transitions and pulsing effects

## 🛠️ Customization Options

### 1. Custom Bus Routes
Modify `FirebaseBusSimulator.kt` to add custom routes:

```kotlin
private val customRoute = listOf(
    BusLocation("busId", lat1, lng1, speed1, heading1, accuracy1, null, "Route Name", true, "driverId", capacity, passengers),
    // Add more waypoints...
)
```

### 2. Custom Map Styling
Update map style in `BusTrackingScreen.kt`:

```kotlin
view.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS) { style ->
    // Custom styling here
}
```

### 3. Custom Colors
Modify `BusTrackingColors` object:

```kotlin
object BusTrackingColors {
    val TRACKING_ACTIVE = Color(0xFF4CAF50)
    val BUS_MARKER = Color(0xFFFF5722)
    // Add more colors...
}
```

## 🐛 Troubleshooting

### Common Issues

1. **Map not loading**
   - Check Mapbox access token
   - Verify internet connection
   - Check Android manifest permissions

2. **Firebase connection failed**
   - Verify `google-services.json` is in correct location
   - Check Firebase project configuration
   - Ensure Realtime Database is enabled

3. **Location permission denied**
   - App will show permission request dialog
   - User can grant permission manually
   - Fallback to default location if denied

4. **Bus not moving**
   - Check Firebase simulator is running
   - Verify database rules allow write access
   - Check network connectivity

### Debug Information

Enable debug logging by adding to your `Application` class:

```kotlin
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Log.d("BusTracking", "Debug mode enabled")
        }
    }
}
```

## 📊 Firebase Database Structure

### Bus Location Data
```json
{
  "buses": {
    "demoBus": {
      "busId": "demoBus",
      "latitude": 22.3072,
      "longitude": 73.1812,
      "speed": 35.0,
      "heading": 45.0,
      "accuracy": 5.0,
      "timestamp": "2024-01-01T10:00:00Z",
      "route": "Route 1 - Vadodara Junction to Alkapuri",
      "isActive": true,
      "driverId": "DRIVER001",
      "capacity": 50,
      "currentPassengers": 25
    }
  }
}
```

### Tracking Session Data
```json
{
  "tracking_sessions": {
    "sessionId": {
      "sessionId": "session_123",
      "userId": "user123",
      "busId": "demoBus",
      "startTime": "2024-01-01T10:00:00Z",
      "status": "ACTIVE",
      "userLocation": {
        "latitude": 22.3000,
        "longitude": 73.1800,
        "accuracy": 10.0,
        "timestamp": "2024-01-01T10:00:00Z"
      },
      "distance": 1500.0
    }
  }
}
```

## 🚀 Production Considerations

### 1. Security Rules
Update Firebase rules for production:

```json
{
  "rules": {
    "buses": {
      ".read": "auth != null",
      ".write": "auth != null && auth.token.admin == true"
    },
    "tracking_sessions": {
      "$sessionId": {
        ".read": "auth != null && (auth.uid == data.child('userId').val() || auth.token.admin == true)",
        ".write": "auth != null && (auth.uid == data.child('userId').val() || auth.token.admin == true)"
      }
    }
  }
}
```

### 2. Performance Optimization
- Implement proper caching for bus locations
- Use pagination for tracking sessions
- Optimize map rendering for large datasets
- Implement offline support

### 3. Error Handling
- Add retry mechanisms for failed requests
- Implement proper error logging
- Add user-friendly error messages
- Handle network connectivity issues

## 📝 API Reference

### BusTrackingViewModel
- `startTracking(busId: String, userId: String)`: Start tracking a bus
- `stopTracking()`: Stop current tracking
- `isTracking: StateFlow<Boolean>`: Current tracking state
- `busLocation: StateFlow<BusLocation?>`: Current bus location
- `userLocation: StateFlow<UserLocation?>`: Current user location

### FirebaseBusSimulator
- `startSimulation()`: Start bus coordinate simulation
- `stopSimulation()`: Stop simulation
- `resetSimulation()`: Reset to starting position
- `isSimulationRunning(): Boolean`: Check simulation status

### FirebaseSetupHelper
- `initializeDemoData()`: Set up Firebase demo data
- `startBusSimulation()`: Start Firebase bus simulation
- `cleanupDemoData()`: Remove all demo data
- `testFirebaseConnection()`: Test Firebase connectivity

## 🎉 Conclusion

This implementation provides a complete, production-ready bus tracking system with:

- ✅ Material3 UI with beautiful animations
- ✅ Real-time Firebase integration
- ✅ Mapbox maps with custom markers and polylines
- ✅ Location permission handling
- ✅ Demo bus simulation
- ✅ Comprehensive error handling
- ✅ ETA and distance calculations
- ✅ Smooth user experience

The system is ready to use and can be easily extended for production use with proper authentication and security rules.
