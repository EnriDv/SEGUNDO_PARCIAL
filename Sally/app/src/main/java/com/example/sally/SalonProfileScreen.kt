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

@Composable
fun SalonProfileScreen(
    navController: NavController,
    initialState: SalonStateBehavior,
    salonData: Salon
) {
    val currentState by remember { mutableStateOf(initialState) }
    var isModalDismissed by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize().background(BackgroundColor)) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .alpha(currentState.contentAlpha)
                .padding(bottom = 100.dp)
        ) {


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
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFCE4EC)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.height(110.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp).width(100.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(service.icon, contentDescription = null, tint = PinkEnd, modifier = Modifier.size(28.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(service.name, fontSize = 13.sp, textAlign = TextAlign.Center, lineHeight = 16.sp, maxLines = 2)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(service.price, color = PurpleStart, fontWeight = FontWeight.Bold)
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
                    navController.navigate("booking/${salonData.name}")
                },
                enabled = currentState.isActionEnabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .shadow(8.dp, RoundedCornerShape(28.dp)),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = currentState.actionButtonColor,
                    disabledContainerColor = currentState.actionButtonColor
                )
            ) {
                Text(currentState.actionButtonText, fontSize = 16.sp, fontWeight = FontWeight.Bold)
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
                                textAlign = TextAlign.Center,
                                color = GrayText,
                                fontSize = 14.sp,
                                lineHeight = 20.sp
                            )

                            Spacer(modifier = Modifier.height(20.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFF3E5F5))
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "¡Vuelve pronto! Estaremos encantados de atenderte.",
                                    color = PurpleStart,
                                    textAlign = TextAlign.Center,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}