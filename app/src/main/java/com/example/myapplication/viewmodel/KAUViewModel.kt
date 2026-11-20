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

object Strings {
    fun get(key: String, isMalayalam: Boolean): String {
        val map = if (isMalayalam) malayalamMap else englishMap
        return map[key] ?: key
    }

    private val englishMap = mapOf(
        "app_title" to "KAU Paddy Field GIS",
        "announcements" to "Announcements",
        "messages" to "Messages & Queries",
        "admin" to "Admin",
        "gis_map" to "GIS Map",
        "search_hint" to "Search Survey No. or Farmer...",
        "total_area" to "Total Area",
        "registered_farmers" to "Registered Farmers",
        "agricultural_officers" to "Agricultural Officers",
        "upload_shapefile" to "Upload Shapefile / GeoJSON / Draw Polygon",
        "mapped_parcels" to "Mapped Paddy Field Parcels",
        "user_rbac" to "User Role & Access Control (RBAC)",
        "krishi_bhavans" to "Krishi Bhavans",
        "audit_logs" to "System Communication Audit Logs",
        "logout" to "Logout",
        "profile" to "User Profile",
        "register_farmer" to "Register New Farmer",
        "add_krishi_bhavan" to "Add New Krishi Bhavan",
        "broadcast" to "Broadcast Announcement",
        "send" to "Send"
    )

    private val malayalamMap = mapOf(
        "app_title" to "കെ.എ.യു നെൽവയൽ ജി.ഐ.എസ്",
        "announcements" to "അറിയിപ്പുകൾ",
        "messages" to "സന്ദേശങ്ങളും സംശയങ്ങളും",
        "admin" to "ഭരണാധികാരി",
        "gis_map" to "ഭൂപടം",
        "search_hint" to "സർവേ നമ്പറോ കർഷകനോ തിരയുക...",
        "total_area" to "ആകെ വിസ്തൃതി",
        "registered_farmers" to "രജിസ്റ്റർ ചെയ്ത കർഷകർ",
        "agricultural_officers" to "കൃഷി ഉദ്യോഗസ്ഥർ",
        "upload_shapefile" to "ഷേപ്പ്ഫൈൽ / ജിയോജെസൺ അപ്‌ലോഡ് ചെയ്യുക",
        "mapped_parcels" to "ഭൂപടം തയ്യാറാക്കിയ നെൽവയലുകൾ",
        "user_rbac" to "ഉപയോക്തൃ റോൾ നിയന്ത്രണം (RBAC)",
        "krishi_bhavans" to "കൃഷിഭവനുകൾ",
        "audit_logs" to "സിസ്റ്റം ആശയവിനിമയ ലോഗുകൾ",
        "logout" to "പുറത്തിറങ്ങുക",
        "profile" to "ഉപയോക്തൃ പ്രൊഫൈൽ",
        "register_farmer" to "പുതിയ കർഷകനെ രജിസ്റ്റർ ചെയ്യുക",
        "add_krishi_bhavan" to "പുതിയ കൃഷിഭവൻ ചേർക്കുക",
        "broadcast" to "അറിയിപ്പ് നൽകുക",
        "send" to "അയയ്ക്കുക"
    )
}

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

    var isMalayalam by mutableStateOf(false)
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

    fun toggleLanguage() {
        isMalayalam = !isMalayalam
    }

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
            latitude = lat,
            longitude = lon,
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
