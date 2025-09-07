# 🚌 Android Bus Tracking System

A complete Android Jetpack Compose + Material3 bus tracking application with Mapbox Maps SDK and Firebase Realtime Database integration.

## ✨ Features

- **🎨 Material3 UI**: Modern, beautiful interface with proper theming and animations
- **🗺️ Mapbox Integration**: Real-time map with custom markers, polylines, and smooth animations
- **🔥 Firebase Realtime Database**: Live bus location updates every 2 seconds
- **📍 Location Services**: Runtime permission handling for user location
- **🚌 Demo Bus Simulator**: Automatic bus movement simulation around Vadodara Junction
- **📊 Real-time Tracking**: Live ETA, distance, and route information
- **🛡️ Error Handling**: Comprehensive error handling for all edge cases
- **📱 Responsive Design**: Works on all screen sizes with proper Material3 design

## 🚀 Quick Start

### 1. Prerequisites
- Android Studio (latest version)
- Firebase project with Realtime Database enabled
- Mapbox account with access token
- Android device/emulator with location services

### 2. Setup
1. **Firebase Setup**:
   - Create Firebase project
   - Enable Realtime Database
   - Download `google-services.json` to `app/` directory
   - Set database rules (see integration guide)

2. **Mapbox Setup**:
   - Get Mapbox access token
   - Token is already configured in the project

3. **Run the App**:
   ```bash
   ./gradlew assembleDebug
   ```

### 3. Usage
1. Launch the app
2. Grant location permission when prompted
3. Click "Start Tracking" button
4. Watch the bus move in real-time on the map
5. See live ETA and distance updates

## 📁 Project Structure

```
app/src/main/java/com/example/userpanelnew/
├── ui/
│   ├── screens/
│   │   └── BusTrackingScreen.kt          # Main tracking screen
│   ├── activities/
│   │   └── BusTrackingDemoActivity.kt    # Demo activity
│   └── components/
│       └── EnhancedMapScreen.kt          # Enhanced map component
├── services/
│   ├── BusTrackingService.kt             # Firebase bus tracking service
│   ├── DemoTrackingService.kt            # Demo tracking service
│   └── FirebaseBusSimulator.kt           # Firebase bus simulator
├── viewmodels/
│   └── BusTrackingViewModel.kt           # Main tracking view model
├── models/
│   └── BusTracking.kt                    # Data models
└── utils/
    ├── LocationHelper.kt                 # Location utilities
    └── FirebaseSetupHelper.kt            # Firebase setup utilities
```

## 🔧 Key Components

### BusTrackingScreen
The main screen that provides:
- Mapbox MapView with AndroidView integration
- Material3 UI components
- Real-time bus tracking
- Location permission handling
- Error handling and user feedback

### FirebaseBusSimulator
Simulates a bus moving around Vadodara Junction:
- Writes coordinates to Firebase every 2 seconds
- Follows realistic route with 25+ waypoints
- Includes speed, heading, and passenger data
- Automatic start/stop functionality

### BusTrackingViewModel
Manages all tracking state:
- Firebase integration
- Location updates
- Demo data initialization
- Error handling
- State management with StateFlow

## 🎯 Demo Features

### Bus Route
The demo bus follows a realistic route around Vadodara Junction:
- **Starting Point**: Vadodara Junction (22.3072, 73.1812)
- **Route**: Junction → Sayajigunj → Alkapuri → Race Course → Railway Station → Back to Junction
- **Duration**: ~50 seconds for complete route
- **Updates**: Every 2 seconds with realistic data

### Real-time Data
- **Bus Location**: Latitude, longitude, speed, heading
- **User Location**: Current user position with accuracy
- **Distance**: Real-time distance calculation
- **ETA**: Estimated time of arrival based on bus speed
- **Passenger Count**: Simulated passenger data

## 🛠️ Customization

### Custom Routes
Modify `FirebaseBusSimulator.kt` to add custom routes:

```kotlin
private val customRoute = listOf(
    BusLocation("busId", lat1, lng1, speed1, heading1, accuracy1, null, "Route Name", true, "driverId", capacity, passengers),
    // Add more waypoints...
)
```

### Custom Styling
Update colors in `BusTrackingColors`:

```kotlin
object BusTrackingColors {
    val TRACKING_ACTIVE = Color(0xFF4CAF50)
    val BUS_MARKER = Color(0xFFFF5722)
    // Add more colors...
}
```

### Custom Map Style
Change map style in `BusTrackingScreen.kt`:

```kotlin
view.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS) { style ->
    // Custom styling here
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

## 🔒 Security & Production

### Development Rules
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

### Production Rules
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

## 📱 Screenshots

The app provides a beautiful Material3 interface with:
- Full-screen Mapbox map
- Floating action button for start/stop tracking
- Live status card with ETA and distance
- Smooth animations and transitions
- Error handling with user-friendly messages

## 🚀 Performance

- **Efficient**: Uses StateFlow for reactive state management
- **Smooth**: 60fps animations and transitions
- **Optimized**: Proper lifecycle management and cleanup
- **Responsive**: Works on all screen sizes
- **Battery-friendly**: Optimized location updates

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

## 📚 Additional Resources

- [Integration Guide](BUS_TRACKING_INTEGRATION_GUIDE.md) - Detailed setup instructions
- [Firebase Documentation](https://firebase.google.com/docs)
- [Mapbox Android SDK](https://docs.mapbox.com/android/)
- [Material3 Design System](https://m3.material.io/)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)

---

**Built with ❤️ using Android Jetpack Compose, Material3, Mapbox, and Firebase**