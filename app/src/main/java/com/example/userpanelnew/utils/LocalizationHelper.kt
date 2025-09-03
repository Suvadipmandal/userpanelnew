package com.example.userpanelnew.utils

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import com.example.userpanelnew.models.AppLanguage
import java.util.Locale

object LocalizationHelper {
    
    fun setLocale(context: Context, language: AppLanguage): Context {
        val locale = Locale(language.code)
        Locale.setDefault(locale)
        
        val config = Configuration(context.resources.configuration)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(locale)
        } else {
            config.locale = locale
        }
        
        return context.createConfigurationContext(config)
    }
    
    fun updateResources(context: Context, language: AppLanguage): Context {
        val locale = Locale(language.code)
        Locale.setDefault(locale)
        
        val resources: Resources = context.resources
        val config: Configuration = resources.configuration
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(locale)
        } else {
            config.locale = locale
        }
        
        return context.createConfigurationContext(config)
    }
    
    fun getCurrentLocale(context: Context): Locale {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.resources.configuration.locales[0]
        } else {
            context.resources.configuration.locale
        }
    }
    
    fun getLanguageFromLocale(locale: Locale): AppLanguage {
        return when (locale.language) {
            "hi" -> AppLanguage.HINDI
            "gu" -> AppLanguage.GUJARATI
            "mr" -> AppLanguage.MARATHI
            "te" -> AppLanguage.TELUGU
            "bn" -> AppLanguage.BENGALI
            else -> AppLanguage.ENGLISH
        }
    }
}





