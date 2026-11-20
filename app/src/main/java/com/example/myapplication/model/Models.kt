package com.example.myapplication.model

data class User(
    val id: Int,
    val name: String,
    val phone: String,
    val email: String,
    val role: String, // 'farmer', 'officer', 'admin'
    val krishiBhavanId: Int
)

data class PaddyField(
    val id: Int,
    val surveyNumber: String,
    val farmerId: Int,
    val farmerName: String,
    val farmerPhone: String,
    val krishiBhavanId: Int,
    val areaHa: Double,
    val cropSeason: String, // 'Virippu', 'Mundakan', 'Puncha'
    val soilType: String,
    val latitude: Double,
    val longitude: Double,
    val attributes: Map<String, String>
)

data class Announcement(
    val id: Int,
    val officerId: Int,
    val krishiBhavanId: Int,
    val title: String,
    val body: String,
    val createdAt: String
)

data class Message(
    val id: Int,
    val senderId: Int,
    val receiverId: Int,
    val body: String,
    val createdAt: String
)
