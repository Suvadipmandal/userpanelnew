package com.example.userpanelnew.data

import com.example.userpanelnew.models.*
import kotlinx.coroutines.delay

class DummyDataRepository {
    
    fun getDummyBuses(): List<Bus> {
        return listOf(
            Bus(
                id = "BUS101",
                latitude = 22.3072,
                longitude = 73.1812,
                eta = 6,
                speed = 35.0,
                lastUpdated = "2 min ago",
                route = "Route 101"
            ),
            Bus(
                id = "BUS102",
                latitude = 22.3090,
                longitude = 73.1850,
                eta = 8,
                speed = 28.0,
                lastUpdated = "1 min ago",
                route = "Route 102"
            ),
            Bus(
                id = "BUS103",
                latitude = 22.3050,
                longitude = 73.1790,
                eta = 12,
                speed = 42.0,
                lastUpdated = "3 min ago",
                route = "Route 103"
            ),
            Bus(
                id = "BUS104",
                latitude = 22.3120,
                longitude = 73.1880,
                eta = 15,
                speed = 31.0,
                lastUpdated = "4 min ago",
                route = "Route 104"
            )
        )
    }
    
    fun getDummyBusStops(): List<BusStop> {
        return listOf(
            BusStop(
                id = "STOP001",
                name = "Central Station",
                latitude = 22.3072,
                longitude = 73.1812,
                buses = listOf(
                    BusETA("BUS101", 6, "Route 101"),
                    BusETA("BUS103", 12, "Route 103")
                )
            ),
            BusStop(
                id = "STOP002",
                name = "University Square",
                latitude = 22.3090,
                longitude = 73.1850,
                buses = listOf(
                    BusETA("BUS102", 8, "Route 102"),
                    BusETA("BUS104", 15, "Route 104")
                )
            ),
            BusStop(
                id = "STOP003",
                name = "Shopping Mall",
                latitude = 22.3050,
                longitude = 73.1790,
                buses = listOf(
                    BusETA("BUS101", 18, "Route 101"),
                    BusETA("BUS102", 22, "Route 102")
                )
            ),
            BusStop(
                id = "STOP004",
                name = "Hospital Junction",
                latitude = 22.3120,
                longitude = 73.1880,
                buses = listOf(
                    BusETA("BUS103", 25, "Route 103"),
                    BusETA("BUS104", 30, "Route 104")
                )
            )
        )
    }
    
    fun getDummyUser(): User {
        return User(
            id = "USER001",
            name = "John Doe",
            email = "john.doe@example.com",
            phone = "+91 98765 43210"
        )
    }
    
    suspend fun getBusesWithDelay(): List<Bus> {
        delay(1000) // Simulate network delay
        return getDummyBuses()
    }
    
    suspend fun getBusStopsWithDelay(): List<BusStop> {
        delay(1000) // Simulate network delay
        return getDummyBusStops()
    }
}
