# Notification Permission Feature

## Overview
The app now includes a notification permission dialog that appears when the app opens, similar to other modern apps. This allows users to receive real-time bus updates, ETA notifications, and other important information in the notification bar.

## How It Works

### Permission Flow
1. **App Launch**: When the app opens, it first checks for location permissions
2. **Location Permission**: If location permission is not granted, the location permission dialog appears first
3. **Notification Permission**: After location permission is granted, the notification permission dialog appears
4. **User Choice**: Users can either allow or deny notifications

### Features
- **Real-time Bus Updates**: Get notified about bus arrival times (ETA)
- **Route Changes**: Receive alerts about route changes and delays
- **Service Updates**: Important service announcements
- **Bus Location Updates**: Track your bus location in real-time

### User Experience
- **Beautiful Dialog**: Modern, user-friendly notification permission dialog
- **Clear Benefits**: Explains what notifications will be used for
- **Easy Control**: Users can change notification settings anytime in device settings
- **Feedback**: Toast messages confirm permission status

## Technical Implementation

### Files Modified/Created
1. **AndroidManifest.xml**: Added `POST_NOTIFICATIONS` permission
2. **MainViewModel.kt**: Added notification permission state management
3. **NotificationPermissionDialog.kt**: New component for notification permission dialog
4. **MainApp.kt**: Integrated notification permission flow

### Permission States
- `notificationPermissionGranted`: Tracks whether notification permission is granted
- Automatic permission checking on app startup
- Sequential permission requests (location first, then notifications)

### Dialog Features
- **Icon**: Notification icon for visual appeal
- **Title**: "Enable Notifications"
- **Description**: Clear explanation of notification benefits
- **Benefits List**: 
  - Bus arrival times (ETA)
  - Route changes and delays
  - Important service updates
  - Bus location updates
- **Settings Note**: Reminder that users can change settings later
- **Action Buttons**: "Allow Notifications" and "Not Now"

## Usage
The notification permission dialog will automatically appear when:
1. The app is opened for the first time
2. Location permission has been granted
3. Notification permission has not been granted yet

Users can enable notifications later by going to their device's app settings.

## Benefits
- **Better User Engagement**: Users stay informed about their bus
- **Real-time Updates**: No need to constantly check the app
- **Improved Experience**: Proactive notifications for delays and changes
- **Modern UX**: Follows Android best practices for permission requests
