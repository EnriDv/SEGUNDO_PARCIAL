package com.example.sally

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.outlined.Help
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun ProfileScreen(navController: NavController) {
    val context = LocalContext.current
    val dao = remember { AppDatabase.getDatabase(context).appointmentDao() }

    val allAppointments by dao.getAllAppointments().collectAsState(initial = emptyList())

    val currentTime = System.currentTimeMillis()
    val activeCount = allAppointments.count {
        it.status == "Active" && it.date >= (currentTime - 86400000)
    }
    val historyCount = allAppointments.count {
        it.status == "Cancelled" || (it.status == "Active" && it.date < (currentTime - 86400000))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 100.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .clip(RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp))
                    .background(MainGradient)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box {
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .border(3.dp, Color.White, CircleShape)
                                .background(Color.Gray)
                        )
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .align(Alignment.BottomEnd)
                                .clip(CircleShape)
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, tint = PurpleStart, modifier = Modifier.size(16.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Jane Doe", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Text("jhon.doe@email.com", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)

                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {},
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.2f)),
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.5f))
                    ) {
                        Text("Editar Perfil", color = Color.White)
                    }
                }
            }

            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .offset(y = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatsCard(
                    title = "Mis Citas",
                    value = activeCount.toString(),
                    color = Color(0xFF9810FA),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { navController.navigate("appointments/0") }
                )
                StatsCard(
                    title = "Historial",
                    value = historyCount.toString(),
                    color = Color(0xFFE60076),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { navController.navigate("appointments/1") }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        MenuSection(title = "Configuración de Cuenta") {
            MenuItem(icon = Icons.Outlined.Person, text = "Información Personal", subtitle = "Actualiza tus datos")
            HorizontalDivider(modifier = Modifier.padding(start = 56.dp), color = Color.LightGray.copy(alpha = 0.3f))
            MenuItem(icon = Icons.Outlined.Email, text = "Email y Contraseña", subtitle = "Gestiona tu acceso")
        }

        MenuSection(title = "Preferencias") {
            MenuItem(icon = Icons.Outlined.Notifications, text = "Notificaciones", subtitle = "Recibe alertas de citas", hasSwitch = true)
            HorizontalDivider(modifier = Modifier.padding(start = 56.dp), color = Color.LightGray.copy(alpha = 0.3f))
            MenuItem(icon = Icons.Outlined.Language, text = "Idioma", subtitle = "Español")
            HorizontalDivider(modifier = Modifier.padding(start = 56.dp), color = Color.LightGray.copy(alpha = 0.3f))
            MenuItem(icon = Icons.Outlined.Lock, text = "Privacidad", subtitle = "Controla tu información")
        }

        MenuSection(title = "Ayuda y Soporte") {
            MenuItem(icon = Icons.AutoMirrored.Outlined.Help, text = "Centro de Ayuda", subtitle = "FAQ y tutoriales")
            HorizontalDivider(modifier = Modifier.padding(start = 56.dp), color = Color.LightGray.copy(alpha = 0.3f))
            MenuItem(icon = Icons.Outlined.Mail, text = "Contactar Soporte", subtitle = "Estamos aquí para ayudarte")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            shape = RoundedCornerShape(12.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, tint = Color.Red)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Cerrar Sesión", color = Color.Red, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun StatsCard(title: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(80.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, color = Color.White.copy(alpha = 0.9f), fontSize = 12.sp)
            Text(value, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun MenuSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)) {
        Text(title, color = PurpleStart, fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.padding(bottom = 8.dp))
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            content()
        }
    }
}

@Composable
fun MenuItem(icon: ImageVector, text: String, subtitle: String, hasSwitch: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(PurpleStart.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = PurpleStart, modifier = Modifier.size(20.dp))
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(text, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Text(subtitle, color = GrayText, fontSize = 12.sp)
        }

        if (hasSwitch) {
            var checked by remember { mutableStateOf(true) }
            Switch(
                checked = checked,
                onCheckedChange = { checked = it },
                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = PurpleStart)
            )
        } else {
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = GrayText)
        }
    }
}