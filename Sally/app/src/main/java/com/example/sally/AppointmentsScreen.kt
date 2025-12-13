package com.example.sally

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AppointmentsScreen(
    navController: NavController,
    initialTab: Int = 0
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val dao = remember { AppDatabase.getDatabase(context).appointmentDao() }

    val appointmentsList by dao.getAllAppointments().collectAsState(initial = emptyList())

    var selectedTab by remember { mutableStateOf(initialTab) }

    val currentTime = System.currentTimeMillis()

    val activeAppointments = appointmentsList.filter {
        it.status == "Active" && it.date >= (currentTime - 86400000)
    }

    val historyAppointments = appointmentsList.filter {
        it.status == "Cancelled" || (it.status == "Active" && it.date < (currentTime - 86400000))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        // --- HEADER ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MainGradient)
                .padding(top = 40.dp, bottom = 20.dp, start = 16.dp, end = 16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBackIosNew, contentDescription = null, tint = Color.White)
                }
                Text(
                    "Mis Citas",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(Color.White, RoundedCornerShape(25.dp))
                .padding(4.dp)
        ) {
            TabButton(
                text = "Activas",
                count = activeAppointments.size,
                isSelected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                modifier = Modifier.weight(1f)
            )
            TabButton(
                text = "Historial",
                count = historyAppointments.size,
                isSelected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                modifier = Modifier.weight(1f)
            )
        }

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val listToShow = if (selectedTab == 0) activeAppointments else historyAppointments

            items(listToShow) { appointment ->
                AppointmentCard(
                    appointment = appointment,
                    isHistory = selectedTab == 1,
                    onCancel = {
                        scope.launch { dao.cancelAppointment(appointment.id) }
                    }
                )
            }
        }
    }
}


@Composable
fun TabButton(text: String, count: Int, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier) {
    Button(
        onClick = onClick,
        modifier = modifier.height(40.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) Color.White else Color.Transparent,
            contentColor = if (isSelected) PurpleStart else Color.Gray
        ),
        elevation = if (isSelected) ButtonDefaults.buttonElevation(defaultElevation = 4.dp) else null,
        shape = RoundedCornerShape(20.dp)
    ) {
        Text(text, fontWeight = FontWeight.Bold)
        if (count > 0) {
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) PurpleStart else Color.Gray),
                contentAlignment = Alignment.Center
            ) {
                Text(count.toString(), color = Color.White, fontSize = 10.sp)
            }
        }
    }
}

@Composable
fun AppointmentCard(
    appointment: Appointment,
    isHistory: Boolean,
    onCancel: () -> Unit
) {
    val dateFormat = SimpleDateFormat("d MMM yyyy", Locale.getDefault())
    val dateStr = dateFormat.format(Date(appointment.date))

    val statusLabel = if (appointment.status == "Cancelled") "Cancelada" else "Completada"
    val statusColor = if (appointment.status == "Cancelled") Color(0xFFFF5252) else Color(0xFF4CAF50)

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.horizontalGradient(listOf(PurpleStart, PinkEnd)))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(appointment.salonName, color = Color.White, fontWeight = FontWeight.Bold)

                if (isHistory) {
                    Box(
                        modifier = Modifier
                            .background(statusColor, RoundedCornerShape(4.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(statusLabel, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFFFD700), RoundedCornerShape(4.dp)) // Amarillo para Próxima
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text("Próxima", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                AppointmentInfoRow(Icons.Default.Event, appointment.serviceName, appointment.price)
                Spacer(modifier = Modifier.height(8.dp))
                AppointmentInfoRow(Icons.Default.Person, "Especialista", appointment.specialistName)
                Spacer(modifier = Modifier.height(8.dp))
                AppointmentInfoRow(Icons.Default.Schedule, "Fecha y hora", "$dateStr a las ${appointment.time}")
                Spacer(modifier = Modifier.height(8.dp))
                AppointmentInfoRow(Icons.Default.LocationOn, "Ubicación", appointment.salonAddress)
            }

            Row(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (!isHistory) {
                    Button(
                        onClick = onCancel,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252)), // Rojo
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Cancelar")
                    }
                }

                Button(
                    onClick = { /* TODO: Navegar a detalles completos */ },
                    colors = ButtonDefaults.buttonColors(containerColor = PurpleStart),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (isHistory) "Detalles" else "Ver Detalles")
                }
            }
        }
    }
}

@Composable
fun AppointmentInfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, value: String) {
    Row(verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(PurpleStart.copy(alpha = 0.1f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = PurpleStart, modifier = Modifier.size(16.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(title, fontSize = 12.sp, color = GrayText)
            Text(value, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}