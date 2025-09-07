package com.example.userpanelnew.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.userpanelnew.models.AppLanguage

class LanguagePreferenceManager(context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences(
        "app_preferences", 
        Context.MODE_PRIVATE
    )
    
    private val LANGUAGE_KEY = "selected_language"
    
    fun saveLanguage(language: AppLanguage) {
        println("LanguagePreferenceManager: Saving language: ${language.displayName}")
        prefs.edit()
            .putString(LANGUAGE_KEY, language.name)
            .apply()
        println("LanguagePreferenceManager: Language saved successfully")
    }
    
    fun getSavedLanguage(): AppLanguage {
        val languageName = prefs.getString(LANGUAGE_KEY, AppLanguage.ENGLISH.name)
        println("LanguagePreferenceManager: Retrieved language name: $languageName")
        return try {
            val language = AppLanguage.valueOf(languageName ?: AppLanguage.ENGLISH.name)
            println("LanguagePreferenceManager: Parsed language: ${language.displayName}")
            language
        } catch (e: IllegalArgumentException) {
            println("LanguagePreferenceManager: Error parsing language, using English: ${e.message}")
            AppLanguage.ENGLISH
        }
    }
    
    fun clearLanguagePreference() {
        prefs.edit()
            .remove(LANGUAGE_KEY)
            .apply()
    }
}
