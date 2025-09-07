# Bus Tracking Notification Feature

## Overview
The app now shows persistent notifications in the Android notification bar when users start tracking a bus. This allows users to see real-time tracking information even when the app is in the background.

## How It Works

### When Notifications Appear
- **Start Tracking**: When users click the "Start Tracking" or play button, a persistent notification appears in the notification bar
- **Real-time Updates**: The notification updates automatically with:
  - Bus ID and route information
  - Current distance from the bus
  - Estimated Time of Arrival (ETA)
  - Live tracking status

### Notification Features
- **Persistent**: Stays visible in the notification bar while tracking is active
- **Interactive**: Users can tap to open the app or use the "Stop Tracking" action button
- **Real-time Updates**: Automatically updates every 5 seconds with new location data
- **Rich Content**: Shows detailed tracking information in expanded view

### Notification Content
- **Title**: "🚌 Tracking Bus [BusID]"
- **Content**: Route, distance, and ETA information
- **Expanded View**: Detailed tracking information including:
  - Bus route details
  - Current distance from bus
  - Estimated arrival time
  - Live tracking status
- **Actions**: "Stop Tracking" button to end tracking

## Technical Implementation

### Files Created/Modified
1. **BusTrackingNotificationService.kt**: Core notification service
2. **BusTrackingNotificationReceiver.kt**: Handles notification actions
3. **NotificationEventBus.kt**: Event communication system
4. **BusTrackingViewModel.kt**: Updated to manage notifications
5. **AndroidManifest.xml**: Added notification permission and receiver
6. **Notification Icons**: Added bus and stop icons

### Key Components

#### Notification Service
- Creates and manages persistent notifications
- Updates notification content with real-time data
- Handles notification channel creation for Android 8.0+

#### Event Bus System
- Enables communication between notification actions and ViewModel
- Handles "Stop Tracking" and "Open App" events
- Uses Kotlin coroutines for async communication

#### Integration Points
- **Start Tracking**: Shows notification when tracking begins
- **Location Updates**: Updates notification with new distance/ETA
- **Stop Tracking**: Hides notification when tracking ends
- **Background Operation**: Continues updating even when app is backgrounded

## User Experience

### Starting Tracking
1. User clicks "Start Tracking" button
2. Notification appears in notification bar
3. Real-time updates begin automatically

### During Tracking
- Notification shows current bus information
- Updates every 5 seconds with new data
- Users can tap notification to open app
- Users can use "Stop Tracking" action to end tracking

### Stopping Tracking
- User can stop from app or notification
- Notification disappears immediately
- All tracking data is cleared

## Benefits
- **Background Awareness**: Users can see tracking info without opening app
- **Battery Efficient**: Uses system notification system
- **User Control**: Easy to stop tracking from notification
- **Real-time Updates**: Always shows current tracking status
- **Modern UX**: Follows Android notification best practices

## Permissions Required
- `POST_NOTIFICATIONS`: Required for Android 13+ to show notifications
- `ACCESS_FINE_LOCATION`: Required for accurate tracking
- `ACCESS_COARSE_LOCATION`: Fallback location permission

## Testing
The feature has been tested and verified to:
- ✅ Show notifications when tracking starts
- ✅ Update notifications with real-time data
- ✅ Handle notification actions correctly
- ✅ Hide notifications when tracking stops
- ✅ Work in background mode
- ✅ Compile and build successfully
