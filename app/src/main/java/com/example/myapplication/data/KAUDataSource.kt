package com.example.myapplication.data

import com.example.myapplication.model.Announcement
import com.example.myapplication.model.Message
import com.example.myapplication.model.PaddyField
import com.example.myapplication.model.User

object KAUDataSource {
    val users = listOf(
        User(1, "Dr. Ananya Nair (Officer)", "9847012345", "officer.mannuthy@kau.in", "officer", 1),
        User(2, "Rajan P.K. (Farmer)", "9447123456", "rajan.farmer@gmail.com", "farmer", 1),
        User(3, "Sreedharan V. (Farmer)", "9747234567", "sreedharan.farmer@gmail.com", "farmer", 1),
        User(4, "Admin User", "9000000000", "admin@kau.in", "admin", 1)
    )

    val paddyFields = mutableListOf(
        PaddyField(
            id = 1,
            surveyNumber = "SY-142/2A",
            farmerId = 2,
            farmerName = "Rajan P.K.",
            farmerPhone = "9447123456",
            krishiBhavanId = 1,
            areaHa = 0.75,
            cropSeason = "Virippu",
            soilType = "Clay Loam",
            latitude = 10.5470,
            longitude = 76.2900,
            attributes = mapOf(
                "Variety" to "Uma (MO 16)",
                "Irrigation" to "Canal",
                "Fertilizer Status" to "Applied NPK",
                "Yield Estimate (Tons)" to "3.2"
            )
        ),
        PaddyField(
            id = 2,
            surveyNumber = "SY-143/1B",
            farmerId = 2,
            farmerName = "Rajan P.K.",
            farmerPhone = "9447123456",
            krishiBhavanId = 1,
            areaHa = 1.20,
            cropSeason = "Mundakan",
            soilType = "Alluvial Clay",
            latitude = 10.5500,
            longitude = 76.2950,
            attributes = mapOf(
                "Variety" to "Jyothi",
                "Irrigation" to "Borewell",
                "Fertilizer Status" to "Pending Organic",
                "Yield Estimate (Tons)" to "5.0"
            )
        ),
        PaddyField(
            id = 3,
            surveyNumber = "SY-155/4C",
            farmerId = 3,
            farmerName = "Sreedharan V.",
            farmerPhone = "9747234567",
            krishiBhavanId = 1,
            areaHa = 0.50,
            cropSeason = "Puncha",
            soilType = "Acid Sulfate",
            latitude = 10.5400,
            longitude = 76.2820,
            attributes = mapOf(
                "Variety" to "Matta Thrissivaperoor",
                "Irrigation" to "Rainfed",
                "Fertilizer Status" to "Applied Lime",
                "Yield Estimate (Tons)" to "2.1"
            )
        )
    )

    val announcements = mutableListOf(
        Announcement(
            id = 1,
            officerId = 1,
            krishiBhavanId = 1,
            title = "Paddy Harvesting Advisory - Virippu Season",
            body = "Farmers in Mannuthy jurisdiction are advised to arrange combine harvesters ahead of expected monsoon showers next week.",
            createdAt = "2025-03-01 10:00"
        ),
        Announcement(
            id = 2,
            officerId = 1,
            krishiBhavanId = 1,
            title = "Pesticide Subsidy Distribution",
            body = "Subsidy for organic neem-based pest control is available at Krishi Bhavan. Bring Aadhaar and land tax receipt.",
            createdAt = "2025-02-28 14:30"
        )
    )

    val messages = mutableListOf(
        Message(
            id = 1,
            senderId = 2,
            receiverId = 1,
            body = "Madam, my field SY-142/2A requires water pump inspection.",
            createdAt = "2025-03-02 09:15"
        ),
        Message(
            id = 2,
            senderId = 1,
            receiverId = 2,
            body = "Rajan, maintenance team has been notified and will visit tomorrow morning.",
            createdAt = "2025-03-02 10:30"
        )
    )
}
