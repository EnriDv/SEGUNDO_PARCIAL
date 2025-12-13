package com.example.sally

import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.google.android.gms.maps.model.LatLng

data class Service(val name: String, val icon: ImageVector, val price: String)
data class Specialist(val name: String, val rating: String, val color: Color)

data class Salon(
    val id: Int,
    val name: String,
    val rating: String,
    val reviews: String,
    val coverColor: Color,
    val isClosed: Boolean,
    val location: LatLng,
    val address: String
)

val mockServices = listOf(
    Service("Corte & Peinado", Icons.Outlined.ContentCut, "$45"),
    Service("Manicure Gel", Icons.Outlined.Brush, "$35"),
    Service("Masaje Relax", Icons.Outlined.Spa, "$60"),
    Service("Maquillaje Pro", Icons.Outlined.Face, "$50"),
    Service("Tinte Completo", Icons.Outlined.Palette, "$80"),
    Service("Limpieza Facial", Icons.Outlined.CleanHands, "$40")
)

val mockSpecialists = listOf(
    Specialist("María García", "5.0", Color(0xFFE1BEE7)),
    Specialist("Ana Rodríguez", "4.8", Color(0xFFFFCCBC)),
    Specialist("Sofía López", "4.9", Color(0xFFC5CAE9)),
    Specialist("Laura Martínez", "4.7", Color(0xFFB2DFDB)),
    Specialist("Carlos Ruiz", "5.0", Color(0xFFFFECB3))
)

val mockSalons = listOf(
    Salon(
        0, "Luxe Spa & Beauty", "4.8", "(234)", Color(0xFFE1BEE7), false,
        LatLng(-17.783396, -63.182061), // Plaza 24 de Septiembre (Centro)
        "Calle Ayacucho, Casco Viejo"
    ),
    Salon(
        1, "Elegant Hair Studio", "4.9", "(189)", Color(0xFFB39DDB), true,
        LatLng(-17.756185, -63.194165), // Zona Equipetrol (Cerca de Ventura Mall)
        "Av. San Martín, Equipetrol"
    ),
    Salon(
        2, "Urban Barbershop", "4.7", "(120)", Color(0xFF90CAF9), false,
        LatLng(-17.771343, -63.168852), // Zona Cine Center (2do Anillo)
        "Av. El Trompillo, Zona Sur"
    ),
    Salon(
        3, "Natural Glow", "5.0", "(56)", Color(0xFFA5D6A7), true,
        LatLng(-17.733560, -63.168430), // Zona Av. Banzer (Cerca de Las Brisas)
        "Av. Cristo Redentor, Norte"
    )
)

interface SalonStateBehavior {
    val contentAlpha: Float
    val isDialogVisible: Boolean
    val actionButtonText: String
    val isActionEnabled: Boolean
    val actionButtonColor: Color
}

class OpenState : SalonStateBehavior {
    override val contentAlpha: Float = 1f
    override val isDialogVisible: Boolean = false
    override val actionButtonText: String = "+ AGENDAR UNA CITA"
    override val isActionEnabled: Boolean = true
    override val actionButtonColor: Color = Color(0xFF9810FA)
}

class ClosedState : SalonStateBehavior {
    override val contentAlpha: Float = 0.3f
    override val isDialogVisible: Boolean = true
    override val actionButtonText: String = "NO DISPONIBLE"
    override val isActionEnabled: Boolean = false
    override val actionButtonColor: Color = Color.Gray
}