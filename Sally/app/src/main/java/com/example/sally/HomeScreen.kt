package com.example.sally

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(navController: NavController) {
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(3000)
        isLoading = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 80.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth().height(180.dp)
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .shimmerEffect()
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth().height(180.dp)
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(MainGradient)
            ) {
                Column(modifier = Modifier.align(Alignment.CenterStart).padding(20.dp)) {
                    Text("Descubre tu belleza", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Los mejores salones cerca de ti", color = Color.White.copy(alpha = 0.9f), fontSize = 14.sp)
                }
            }
        }

        SectionHeader("Servicios")
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (isLoading) {
                items(5) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(modifier = Modifier.size(70.dp).clip(CircleShape).shimmerEffect())
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(modifier = Modifier.height(12.dp).width(50.dp).shimmerEffect())
                    }
                }
            } else {
                items(mockServices) { service ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(70.dp)
                                .clip(CircleShape)
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(service.icon, contentDescription = null, tint = PinkEnd)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(service.name, fontSize = 12.sp)
                    }
                }
            }
        }

        SectionHeader("Salones Recomendados")
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (isLoading) {
                items(3) {
                    Box(modifier = Modifier.width(200.dp).height(180.dp).clip(RoundedCornerShape(16.dp)).shimmerEffect())
                }
            } else {
                items(mockSalons) { salon ->
                    SalonCard(salon = salon, onClick = { navController.navigate("salon_profile/${salon.id}") })
                }
            }
        }
    }
}