package com.example.myapplication.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.myapplication.data.KAUDataSource
import com.example.myapplication.model.Announcement
import com.example.myapplication.model.Message
import com.example.myapplication.model.PaddyField
import com.example.myapplication.model.User

data class KrishiBhavan(
    val id: Int,
    val name: String,
    val district: String
)

class KAUViewModel : ViewModel() {
    var currentUser by mutableStateOf<User?>(null)
        private set

    var paddyFields by mutableStateOf<List<PaddyField>>(KAUDataSource.paddyFields)
        private set

    var announcements by mutableStateOf<List<Announcement>>(KAUDataSource.announcements)
        private set

    var messages by mutableStateOf<List<Message>>(KAUDataSource.messages)
        private set

    var users by mutableStateOf<List<User>>(KAUDataSource.users)
        private set

    var krishiBhavans by mutableStateOf<List<KrishiBhavan>>(
        listOf(
            KrishiBhavan(1, "Mannuthy Krishi Bhavan", "Thrissur"),
            KrishiBhavan(2, "Ollukkara Krishi Bhavan", "Thrissur"),
            KrishiBhavan(3, "Puthukkad Krishi Bhavan", "Thrissur")
        )
    )
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun login(email: String): Boolean {
        val user = users.find { it.email.equals(email, ignoreCase = true) }
        if (user != null) {
            currentUser = user
            errorMessage = null
            return true
        }
        errorMessage = "User not found with email: $email"
        return false
    }

    fun logout() {
        currentUser = null
    }

    fun broadcastAnnouncement(title: String, body: String) {
        val officer = currentUser ?: return
        val newAnn = Announcement(
            id = announcements.size + 1,
            officerId = officer.id,
            krishiBhavanId = officer.krishiBhavanId,
            title = title,
            body = body,
            createdAt = "Just now"
        )
        announcements = listOf(newAnn) + announcements
    }

    fun sendMessage(receiverId: Int, body: String) {
        val sender = currentUser ?: return
        val newMsg = Message(
            id = messages.size + 1,
            senderId = sender.id,
            receiverId = receiverId,
            body = body,
            createdAt = "Just now"
        )
        messages = messages + newMsg
    }

    fun addFarmer(name: String, phone: String, email: String, krishiBhavanId: Int) {
        val newFarmer = User(
            id = users.size + 1,
            name = name,
            phone = phone,
            email = email,
            role = "farmer",
            krishiBhavanId = krishiBhavanId
        )
        users = users + newFarmer
    }

    fun addPaddyField(
        surveyNumber: String,
        farmerName: String,
        areaHa: Double,
        cropSeason: String,
        soilType: String,
        lat: Double,
        lon: Double
    ) {
        val farmer = users.find { it.name.contains(farmerName, ignoreCase = true) && it.role == "farmer" }
            ?: users.firstOrNull { it.role == "farmer" }
            ?: User(2, farmerName, "9000000000", "farmer@kau.in", "farmer", 1)

        // Apply a smart spatial offset if coordinates are identical to existing fields
        val offsetIndex = paddyFields.size
        val adjustedLat = lat + (offsetIndex * 0.003)
        val adjustedLon = lon + (offsetIndex * 0.003)

        val newField = PaddyField(
            id = paddyFields.size + 1,
            surveyNumber = surveyNumber,
            farmerId = farmer.id,
            farmerName = farmer.name,
            farmerPhone = farmer.phone,
            krishiBhavanId = 1,
            areaHa = areaHa,
            cropSeason = cropSeason,
            soilType = soilType,
            latitude = adjustedLat,
            longitude = adjustedLon,
            attributes = mapOf(
                "Variety" to "Uma (MO 16)",
                "Irrigation" to "Canal/Borewell",
                "Fertilizer Status" to "Admin Verified Shapefile",
                "Yield Estimate (Tons)" to "${String.format("%.1f", areaHa * 4.2)}"
            )
        )
        paddyFields = paddyFields + newField
    }

    fun deletePaddyField(fieldId: Int) {
        paddyFields = paddyFields.filter { it.id != fieldId }
    }

    fun updateUserRole(userId: Int, newRole: String) {
        users = users.map { if (it.id == userId) it.copy(role = newRole) else it }
    }

    fun deleteUser(userId: Int) {
        users = users.filter { it.id != userId }
    }

    fun addKrishiBhavan(name: String, district: String) {
        val newKb = KrishiBhavan(
            id = krishiBhavans.size + 1,
            name = name,
            district = district
        )
        krishiBhavans = krishiBhavans + newKb
    }
}
