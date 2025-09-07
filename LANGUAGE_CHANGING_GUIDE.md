# Language Changing Implementation Guide

## Overview
This guide explains the efficient language changing functionality implemented in your Jetpack Compose Material3 app.

## Features
- ✅ **Persistent Language Storage**: Uses SharedPreferences to remember user's language choice
- ✅ **Real-time UI Updates**: Language changes are reflected immediately without app restart
- ✅ **Material3 Design**: Beautiful, modern UI components following Material3 guidelines
- ✅ **Reusable Components**: LanguageSelector can be used in any screen
- ✅ **Multi-language Support**: Supports English, Hindi, Gujarati, Marathi, Telugu, and Bengali

## Implementation Details

### 1. LanguageSelector Component
**Location**: `ui/components/LanguageSelector.kt`

A reusable composable that provides:
- Clean Material3 card design
- Language selection dialog
- Current language display
- Easy integration in any screen

### 2. LanguagePreferenceManager
**Location**: `utils/LanguagePreferenceManager.kt`

Handles persistent storage:
- Saves language preference to SharedPreferences
- Loads saved language on app startup
- Provides fallback to English if no preference exists

### 3. MainViewModel Integration
**Location**: `viewmodels/MainViewModel.kt`

Enhanced with:
- Language preference manager initialization
- Automatic language persistence
- StateFlow for reactive UI updates

### 4. ProfileScreen Integration
**Location**: `ui/screens/ProfileScreen.kt`

Updated with:
- Language selector component
- Localized strings for all UI text
- Real-time language switching

## Usage Examples

### Basic Usage in Any Screen
```kotlin
@Composable
fun YourScreen(viewModel: MainViewModel) {
    val currentLanguage by viewModel.currentLanguage.collectAsState()
    
    Column {
        // Your existing content
        
        // Add language selector
        LanguageSelector(
            currentLanguage = currentLanguage,
            onLanguageSelected = { language ->
                viewModel.setLanguage(language)
            }
        )
    }
}
```

### Using Localized Strings
```kotlin
// Instead of hardcoded strings
Text("Hello World")

// Use localized strings
Text(LocalizedStrings.getString("hello_world", currentLanguage))
```

### Adding New Languages
1. Add to `AppLanguage` enum in `models/Models.kt`
2. Add translations to `LocalizedStrings.kt`
3. Update `LocalizationHelper.kt` if needed

## Key Benefits

1. **Efficiency**: No app restart required
2. **User Experience**: Instant language switching
3. **Persistence**: Language choice is remembered
4. **Scalability**: Easy to add new languages
5. **Maintainability**: Clean, modular code structure

## Best Practices

1. **Always use LocalizedStrings**: Don't hardcode text in UI
2. **Initialize early**: Call `initializeLanguagePreferenceManager()` in MainApp
3. **Consistent naming**: Use descriptive keys for localized strings
4. **Test thoroughly**: Verify all languages work correctly

## Future Enhancements

- Add RTL (Right-to-Left) language support
- Implement dynamic language loading from server
- Add language-specific date/time formatting
- Support for custom fonts per language
