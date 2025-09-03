# Mapbox Integration with Jetpack Compose

This project includes a complete Mapbox integration with Jetpack Compose, featuring location permission handling, current location display, and a "My Location" button.

## Features

✅ **Location Permission Handling** - Automatically requests and manages location permissions  
✅ **Current Location Display** - Shows your location with a blue dot and pulsing effect  
✅ **My Location Button** - FloatingActionButton that animates camera to your current location  
✅ **Material 3 Design** - Uses the latest Material 3 components and theming  
✅ **Smooth Camera Animations** - Uses Mapbox's viewport plugin for smooth transitions  

## Files Created

1. **`MyMapScreen.kt`** - Basic Mapbox screen implementation
2. **`MyMapScreenEnhanced.kt`** - Enhanced version with better camera control and error handling
3. **`MyMapViewModel.kt`** - ViewModel for managing map state and location updates

## Dependencies Added

The following dependencies have been added to your project:

```kotlin
// Mapbox dependencies
implementation(libs.mapbox.maps)           // Core Mapbox Maps SDK
implementation(libs.mapbox.annotations)    // Mapbox Annotations
implementation(libs.mapbox.location)       // Mapbox Location Plugin

// Location services
implementation(libs.google.play.services.location)

// Permissions handling
implementation(libs.accompanist.permissions)
```

## Usage

### Basic Usage

```kotlin
@Composable
fun MyApp() {
    MyMapScreen()
}
```

### Enhanced Usage (Recommended)

```kotlin
@Composable
fun MyApp() {
    MyMapScreenEnhanced()
}
```

### Custom ViewModel

```kotlin
@Composable
fun MyApp() {
    val viewModel: MyMapViewModel = viewModel()
    MyMapScreenEnhanced(viewModel = viewModel)
}
```

## Setup Requirements

### 1. Mapbox Access Token

Your Mapbox access token is already configured in `AndroidManifest.xml`:

```xml
<meta-data
    android:name="com.mapbox.token"
    android:value="pk.eyJ1Ijoic3V2YWRpcDEyMzM0IiwiYSI6ImNtZjFqeWlheDFvMGYybHF5dmpsc3o5MHYifQ.yk4ewfwl_81A9KGga7wIMA" />
```

### 2. Permissions

The following permissions are already declared in your manifest:

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

## Key Components

### Location Permission Handling

The screen automatically:
- Checks for existing location permissions
- Requests permissions if not granted
- Shows appropriate UI feedback based on permission status

### Current Location Display

- **Blue Dot**: Mapbox's built-in location component shows your current position
- **Pulsing Effect**: Animated blue circle that pulses around your location
- **Custom Indicator**: Additional animated indicator with scaling and alpha effects

### My Location Button

- **Position**: Bottom-right corner (Material Design standard)
- **Functionality**: Animates camera to your current location
- **Animation**: Smooth 1-second transition with zoom level 15

### Camera Control

The enhanced version uses Mapbox's viewport plugin for:
- Smooth camera transitions
- Configurable animation duration
- Proper zoom levels for location viewing

## Customization

### Change Map Style

```kotlin
getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS) // Streets style
getMapboxMap().loadStyleUri(Style.MAPBOX_SATELLITE) // Satellite style
getMapboxMap().loadStyleUri(Style.MAPBOX_OUTDOORS) // Outdoors style
```

### Modify Pulsing Effect

```kotlin
location.updateSettings {
    pulsingEnabled = true
    pulsingColor = Color.RED // Change color
    pulsingMaxRadius = 30.0 // Change size
    pulsingAnimationDuration = 2000 // Change speed
}
```

### Custom Camera Animation

```kotlin
mapView.viewport.transitionTo(
    CameraOptions.Builder()
        .center(point)
        .zoom(18.0) // Change zoom level
        .build(),
    MapAnimationOptions.Builder()
        .duration(2000L) // Change animation duration
        .build()
)
```

## Troubleshooting

### Common Issues

1. **Map not loading**: Check your internet connection and Mapbox access token
2. **Location not showing**: Ensure location permissions are granted
3. **Camera not animating**: Verify the viewport plugin is properly initialized

### Debug Information

The ViewModel provides detailed state information:
- `isLoading`: Shows loading state
- `hasLocationPermission`: Permission status
- `currentLocation`: Current GPS coordinates
- `error`: Any error messages

## Performance Notes

- The location component automatically handles location updates
- Camera animations are hardware-accelerated
- Pulsing effects use efficient Compose animations
- Map tiles are cached for better performance

## Next Steps

Consider adding these features:
- Custom map markers
- Route planning
- Offline map support
- Custom map styles
- Location search functionality
