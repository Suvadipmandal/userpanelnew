# Google Sign-In Integration Guide

This guide explains how the Google Sign-In functionality has been integrated into the NextStop app.

## Features Added

### 1. Google Sign-In Button
- Added a modern Google Sign-In button to the login screen
- Button includes Google branding and loading states
- Positioned below the email/password login form with a visual divider

### 2. Authentication Flow
- Users can now sign in using their Google account
- Google authentication is integrated with Firebase Auth
- User profile information is automatically extracted from Google account

### 3. Dependencies Added
- `google-auth-library-oauth2`: For Google authentication
- Updated `libs.versions.toml` with the new dependency
- Added to `build.gradle.kts`

## Technical Implementation

### Services Created
1. **GoogleSignInService**: Handles Google Sign-In flow
2. **FirebaseAuthService**: Extended with Google Sign-In method
3. **GoogleSignInButton**: Reusable UI component

### ViewModel Updates
- Added `signInWithGoogle()` method to MainViewModel
- Integrated with existing authentication state management
- Automatic user profile creation from Google account data

### UI Components
- **GoogleSignInButton**: Custom button with Google styling
- **LoginScreen**: Updated with Google Sign-In option
- Visual divider between email/password and Google sign-in

## Configuration

### Google Services Configuration
The `google-services.json` file has been updated with:
- OAuth client configuration for Android
- Web client ID for server-side verification
- Proper package name and certificate hash

### Client IDs Used
- Android Client ID: `23991952685-9c8468q0j6s8ei82ulltjsa78dilfeob.apps.googleusercontent.com`
- Web Client ID: `23991952685-b3qeorbcev63j70sol79r1j5vev6jdm0.apps.googleusercontent.com`

## Usage

### For Users
1. Open the app and navigate to the login screen
2. Choose between:
   - Email/Password login (existing functionality)
   - Google Sign-In (new functionality)
3. Tap "Continue with Google" button
4. Complete Google authentication flow
5. Automatically signed in to the app

### For Developers
The Google Sign-In is fully integrated with the existing authentication system:
- User state is managed through MainViewModel
- Firebase Auth handles the backend authentication
- User profile is automatically created from Google account data

## Error Handling
- Network errors are handled gracefully
- Loading states are shown during authentication
- Error messages are displayed if sign-in fails
- Fallback to email/password login is always available

## Security
- Uses Firebase Auth for secure token management
- Google ID tokens are validated server-side
- User data is handled according to Google's privacy policies
- No sensitive data is stored locally

## Testing
To test the Google Sign-In functionality:
1. Build and run the app
2. Navigate to the login screen
3. Tap the "Continue with Google" button
4. Complete the Google authentication flow
5. Verify that the user is signed in and can access the app

## Troubleshooting
- Ensure Google Services is properly configured
- Check that the correct package name is used in Firebase console
- Verify that the SHA-1 certificate fingerprint is added to Firebase project
- Make sure Google Sign-In is enabled in Firebase Authentication settings
