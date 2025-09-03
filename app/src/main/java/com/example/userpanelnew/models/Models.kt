package com.example.userpanelnew.models

import androidx.compose.runtime.Immutable

@Immutable
data class User(
    val id: String,
    val name: String,
    val email: String,
    val phone: String
)

@Immutable
data class Bus(
    val id: String,
    val latitude: Double,
    val longitude: Double,
    val eta: Int, // in minutes
    val speed: Double, // in km/h
    val lastUpdated: String,
    val route: String
)

@Immutable
data class BusStop(
    val id: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val buses: List<BusETA>
)

@Immutable
data class BusETA(
    val busId: String,
    val eta: Int, // in minutes
    val route: String
)

@Immutable
data class Location(
    val latitude: Double,
    val longitude: Double
)

enum class AppLanguage(val displayName: String, val code: String) {
    ENGLISH("English", "en"),
    HINDI("हिंदी", "hi"),
    GUJARATI("ગુજરાતી", "gu"),
    MARATHI("मराठी", "mr"),
    TELUGU("తెలుగు", "te"),
    BENGALI("বাংলা", "bn")
}
