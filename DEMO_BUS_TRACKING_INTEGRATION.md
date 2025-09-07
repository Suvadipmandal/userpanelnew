# Demo Bus Tracking Integration Guide

This guide provides complete implementation for the "Track Bus (demo)" feature in your Android app.

## 🚀 Quick Start

### 1. Add to Navigation (if using Navigation Compose)

Add this to your navigation graph:

```kotlin
composable("demo_bus_tracking") {
    DemoBusTrackingScreen(
        onNavigateBack = {
            navController.popBackStack()
        }
    )
}
```

### 2. Add Navigation Button

In your main screen, add a button to navigate to demo tracking:

```kotlin
Button(
    onClick = { navController.navigate("demo_bus_tracking") }
) {
    Text("Track Bus (Demo)")
}
```

## 📁 Files Created/Modified

### New Files Created:
1. `repository/DemoBusTrackingRepository.kt` - Firebase Realtime Database integration
2. `ui/screens/DemoBusTrackingScreen.kt` - Main demo tracking screen
3. `ui/components/TrackingNotificationBar.kt` - Tracking status bar
4. `viewmodels/DemoBusTrackingViewModel.kt` - ViewModel for demo tracking
5. `utils/DemoBusSimulator.kt` - Bus movement simulation

### Modified Files:
1. `ui/components/EnhancedMapScreen.kt` - Added proper polyline drawing

## 🔧 Dependencies Required

All required dependencies are already in your `build.gradle.kts`:

```kotlin
// Already present in your project:
implementation(libs.firebase.database) // Firebase Realtime Database
implementation(libs.mapbox.maps) // Mapbox Maps SDK
implementation(libs.accompanist.permissions) // Location permissions
implementation(libs.google.play.services.location) // Location services
```

## 🗺️ Demo Route Details

The demo bus follows realistic routes in Vadodara:

- **Starting Point**: 22.3072, 73.1812 (Center of Vadodara)
- **Route 101**: City Center → Railway Station
- **Route 102**: City Center → Airport  
- **Route 103**: City Center → University

## 🎯 Features Implemented

### ✅ Core Features:
- [x] Demo bus simulation with realistic movement
- [x] Firebase Realtime Database integration
- [x] Real-time location updates every 2 seconds
- [x] User location tracking with permissions
- [x] Polyline between user and bus locations
- [x] Camera animation to fit both markers
- [x] Material3 UI with tracking notification bar
- [x] Start/Stop tracking functionality
- [x] Error handling and permission management

### ✅ UI Components:
- [x] Start Tracking button with loading state
- [x] Live tracking notification bar
- [x] Exit tracking functionality
- [x] Permission request handling
- [x] Error message display
- [x] Back navigation

### ✅ Technical Features:
- [x] ViewModel + Repository architecture
- [x] Kotlin Coroutines for async operations
- [x] StateFlow for reactive UI updates
- [x] Firebase Realtime Database listeners
- [x] Mapbox Maps SDK integration
- [x] Location permission handling
- [x] Battery and bandwidth optimized

## 🚦 Usage Flow

1. **User opens demo tracking screen**
2. **App requests location permission** (if not granted)
3. **User taps "Start Tracking (Demo Bus)"**
4. **App starts demo bus simulation** in Firebase
5. **Real-time updates begin**:
   - Bus location updates every 2 seconds
   - User location updates every 5 seconds
   - Polyline drawn between user and bus
   - Camera fits to show both markers
6. **User can exit tracking** anytime
7. **All resources cleaned up** on exit

## 🔥 Firebase Setup

### Realtime Database Rules:
```json
{
  "rules": {
    "demo_bus_locations": {
      ".read": true,
      ".write": true
    },
    "demo_tracking_sessions": {
      ".read": true,
      ".write": true
    }
  }
}
```

### Database Structure:
```
demo_bus_locations/
  └── DEMO_BUS_001/
      ├── busId: "DEMO_BUS_001"
      ├── latitude: 22.3072
      ├── longitude: 73.1812
      ├── speed: 35.0
      ├── heading: 45.0
      ├── route: "Demo Route 101"
      └── timestamp: [Firebase Timestamp]

demo_tracking_sessions/
  └── demo_session_[timestamp]/
      ├── sessionId: "demo_session_[timestamp]"
      ├── userId: "DEMO_USER_001"
      ├── busId: "DEMO_BUS_001"
      ├── startTime: [Firebase Timestamp]
      ├── userLocation: [UserLocation object]
      └── busLocation: [BusLocation object]
```

## 🎨 Customization Options

### Change Demo Route:
Edit `DemoBusSimulator.kt` to modify routes:

```kotlin
private val demoRoutes = mapOf(
    "ROUTE_101" to listOf(
        Point.fromLngLat(73.1812, 22.3072), // Your coordinates
        // Add more waypoints...
    )
)
```

### Modify Update Intervals:
In `DemoBusTrackingRepository.kt`:

```kotlin
private const val UPDATE_INTERVAL = 2000L // 2 seconds
```

### Change Bus Speed:
In `DemoBusSimulator.kt`:

```kotlin
private const val ROUTE_SPEED_KMH = 35.0 // km/h
```

## 🐛 Troubleshooting

### Common Issues:

1. **Location permission denied**:
   - Check AndroidManifest.xml has location permissions
   - Ensure user grants permission in settings

2. **Firebase connection issues**:
   - Verify `google-services.json` is in app folder
   - Check Firebase project is properly configured

3. **Map not showing**:
   - Verify Mapbox access token is set
   - Check internet connection

4. **Bus not moving**:
   - Check Firebase Realtime Database rules
   - Verify simulation is started properly

## 📱 Testing

### Test Scenarios:
1. **Permission Flow**: Deny → Grant → Use
2. **Network Issues**: Offline → Online
3. **Background**: App in background → Foreground
4. **Multiple Sessions**: Start → Stop → Start again

### Debug Logs:
Enable debug logging by checking Logcat for:
- `DemoBusTrackingViewModel`
- `DemoBusTrackingRepository`
- `DemoBusSimulator`

## 🚀 Production Considerations

### Security:
- Implement proper Firebase security rules
- Add user authentication
- Validate location data

### Performance:
- Implement location caching
- Add offline support
- Optimize battery usage

### Scalability:
- Add multiple bus support
- Implement route management
- Add real-time notifications

## 📞 Support

For issues or questions:
1. Check Firebase console for data flow
2. Verify all dependencies are up to date
3. Test on different devices and Android versions
4. Check Logcat for error messages

---

**Ready to use!** 🎉 Your demo bus tracking feature is now fully implemented and ready for testing.
