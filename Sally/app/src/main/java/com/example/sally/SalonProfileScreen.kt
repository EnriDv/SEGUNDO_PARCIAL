package com.example.sally

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun SalonProfileScreen(
    navController: NavController,
    initialState: SalonStateBehavior,
    salonData: Salon
) {
    val currentState by remember { mutableStateOf(initialState) }
    var isModalDismissed by remember { mutableStateOf(false) }

    var selectedService by remember { mutableStateOf<Service?>(null) }

    Box(modifier = Modifier.fillMaxSize().background(BackgroundColor)) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .alpha(currentState.contentAlpha)
                .padding(bottom = 100.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBackIosNew, contentDescription = null)
                }
                Text("Perfil del Salón", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                Row {
                    IconButton(onClick = {}) { Icon(Icons.Default.Search, contentDescription = null) }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .background(salonData.coverColor)
            )

            Column(modifier = Modifier.padding(16.dp)) {
                Text(salonData.name, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable {
                            navController.navigate("map/${salonData.name}/${salonData.location.latitude}/${salonData.location.longitude}")
                        }
                        .padding(vertical = 4.dp)
                ) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = PurpleStart, modifier = Modifier.size(18.dp))
                    Text(" ${salonData.address}", modifier = Modifier.padding(start = 4.dp), color = GrayText, fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Schedule, contentDescription = null, tint = PurpleStart, modifier = Modifier.size(18.dp))
                    Text(" 9:00 AM - 8:00 PM", modifier = Modifier.padding(start = 4.dp), color = GrayText, fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Phone, contentDescription = null, tint = PurpleStart, modifier = Modifier.size(18.dp))
                    Text(" +531 780 98 145", modifier = Modifier.padding(start = 4.dp), color = GrayText, fontSize = 14.sp)
                }
            }

            SectionHeader("Especialistas")
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(mockSpecialists) { specialist ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(specialist.color)
                                .border(1.dp, Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(specialist.name.take(1), fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(specialist.name.split(" ")[0], fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(10.dp))
                            Text(" ${specialist.rating}", fontSize = 10.sp, color = GrayText)
                        }
                    }
                }
            }

            SectionHeader("Servicios Disponibles")
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(mockServices) { service ->
                    val isSelected = selectedService == service

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) PurpleStart else Color(0xFFFCE4EC)
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .height(110.dp)
                            .clickable { selectedService = service }
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp).width(100.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                service.icon,
                                contentDescription = null,
                                tint = if (isSelected) Color.White else PinkEnd,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                service.name,
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center,
                                lineHeight = 16.sp,
                                maxLines = 2,
                                color = if (isSelected) Color.White else Color.Black
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                service.price,
                                color = if (isSelected) Color.White else PurpleStart,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.White.copy(alpha = 0f), Color.White),
                        startY = 0f,
                        endY = 50f
                    )
                )
                .padding(16.dp)
        ) {
            Button(
                onClick = {
                    if (selectedService != null) {
                        try {
                            val encodedSalonName = URLEncoder.encode(salonData.name, StandardCharsets.UTF_8.toString())
                            val encodedAddress = URLEncoder.encode(salonData.address, StandardCharsets.UTF_8.toString())
                            val encodedServiceName = URLEncoder.encode(selectedService!!.name, StandardCharsets.UTF_8.toString())
                            val encodedPrice = URLEncoder.encode(selectedService!!.price, StandardCharsets.UTF_8.toString())

                            navController.navigate("booking/$encodedSalonName/$encodedAddress/$encodedServiceName/$encodedPrice")
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                },
                enabled = currentState.isActionEnabled && selectedService != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .shadow(8.dp, RoundedCornerShape(28.dp)),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = currentState.actionButtonColor,
                    disabledContainerColor = Color.Gray
                )
            ) {
                val buttonText = if (selectedService == null) "Selecciona un servicio" else currentState.actionButtonText
                Text(buttonText, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }

        if (currentState.isDialogVisible && !isModalDismissed) {
            Dialog(onDismissRequest = { isModalDismissed = true }) {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.padding(16.dp)
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        IconButton(
                            onClick = { isModalDismissed = true },
                            modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = null, tint = GrayText)
                        }

                        Column(
                            modifier = Modifier.padding(top = 34.dp, bottom = 24.dp, start = 24.dp, end = 24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier.size(70.dp).clip(CircleShape).background(MainGradient),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Outlined.Schedule, contentDescription = null, tint = Color.White, modifier = Modifier.size(36.dp))
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Salón Cerrado", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Lo sentimos, actualmente estamos cerrados.\nHorario: Lunes a Sábado\n9:00 AM - 8:00 PM",
                                textAlign = TextAlign.Center, color = GrayText, fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}