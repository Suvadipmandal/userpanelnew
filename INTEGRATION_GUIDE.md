# Mapbox Integration Guide

## 🚀 Quick Integration Steps

### Step 1: Add Map Screen to Navigation
The `Screen.Map` has already been added to your `Navigation.kt` file.

### Step 2: Update MainApp.kt
Add the Map screen to your existing navigation in `MainApp.kt`:

```kotlin
// In your existing navigation logic
when (selectedScreen) {
    Screen.Home -> HomeScreen(viewModel = viewModel)
    Screen.Stops -> StopsScreen(viewModel = viewModel)
    Screen.NearbyBuses -> NearbyBusesScreen(
        viewModel = viewModel,
        onNavigateToHome = { selectedScreen = Screen.Home }
    )
    Screen.Map -> MapScreen()  // Add this line
    Screen.ProfileSettings -> ProfileSettingsScreen(viewModel = viewModel)
    else -> HomeScreen(viewModel = viewModel)
}
```

### Step 3: Add Navigation Item
Add the Map navigation item to your bottom navigation bar:

```kotlin
NavigationBarItem(
    selected = selectedScreen == Screen.Map,
    onClick = { selectedScreen = Screen.Map },
    icon = { 
        Icon(
            Icons.Default.Map,  // or Icons.Default.LocationOn
            contentDescription = "Map"
        ) 
    },
    label = { 
        Text(
            text = "Map",
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = if (selectedScreen == Screen.Map) FontWeight.SemiBold else FontWeight.Medium,
                letterSpacing = 0.1.sp
            ),
            color = if (selectedScreen == Screen.Map) 
                MaterialTheme.colorScheme.primary 
            else 
                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            modifier = Modifier.padding(top = 2.dp)
        ) 
    },
    colors = NavigationBarItemDefaults.colors(
        selectedIconColor = MaterialTheme.colorScheme.primary,
        selectedTextColor = MaterialTheme.colorScheme.primary,
        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
        indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
    )
)
```

## 🧪 Testing the Integration

### Option 1: Test in Existing App
1. Build and run your app
2. Navigate to the Map tab
3. Grant location permissions when prompted
4. See your current location with blue dot and pulsing effect
5. Click the "My Location" button to test camera animation

### Option 2: Test Standalone
1. Launch `MapDemoActivity` directly
2. This shows the Mapbox screen in isolation
3. Useful for testing and debugging

## 🔧 Customization Options

### Change Map Style
```kotlin
// In MyMapScreenEnhanced.kt
getMapboxMap().loadStyleUri(Style.MAPBOX_SATELLITE)  // Satellite view
getMapboxMap().loadStyleUri(Style.MAPBOX_OUTDOORS)   // Outdoors style
```

### Modify Location Indicator
```kotlin
// Change pulsing effect
location.updateSettings {
    pulsingColor = Color.RED           // Change color
    pulsingMaxRadius = 30.0f          // Change size
}
```

### Custom Camera Animation
```kotlin
// Change zoom level and animation
mapView.getMapboxMap().setCamera(
    CameraOptions.Builder()
        .center(point)
        .zoom(18.0)                   // Higher zoom
        .build()
)
```

## 📱 Features Available

✅ **Location Permission Handling** - Automatic permission requests  
✅ **Current Location Display** - Blue dot with pulsing effect  
✅ **My Location Button** - Bottom-right FAB for camera animation  
✅ **Material 3 Design** - Modern UI components  
✅ **Smooth Camera Transitions** - Hardware-accelerated animations  
✅ **Error Handling** - Comprehensive error states and loading indicators  

## 🚨 Troubleshooting

### Common Issues
1. **Map not loading**: Check internet connection and Mapbox token
2. **Location not showing**: Grant location permissions and enable GPS
3. **Build errors**: Ensure all dependencies are properly added

### Debug Commands
```bash
# Clean and rebuild
./gradlew clean build

# Check dependencies
./gradlew app:dependencies | grep mapbox
```

## 🎯 Next Steps

After successful integration, consider adding:
- Custom map markers for bus stops
- Route planning functionality
- Real-time bus location updates
- Offline map support
- Custom map styles

---

**Your Mapbox integration is ready!** 🎉
Build and run to see it in action.
