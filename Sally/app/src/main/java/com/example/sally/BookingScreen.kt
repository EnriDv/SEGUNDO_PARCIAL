package com.example.sally

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(
    navController: NavController,
    salonName: String,
    salonAddress: String,
    serviceName: String,
    servicePrice: String
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val dao = remember { AppDatabase.getDatabase(context).appointmentDao() }

    var hasNotificationPermission by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
            } else {
                true
            }
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted -> hasNotificationPermission = isGranted }
    )

    LaunchedEffect(Unit) {
        NotificationUtils.createNotificationChannel(context)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !hasNotificationPermission) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    val datePickerState = rememberDatePickerState()
    var selectedTime by remember { mutableStateOf<String?>(null) }
    var selectedSpecialist by remember { mutableStateOf<Specialist?>(null) }

    val decodedAddress = remember(salonAddress) { URLDecoder.decode(salonAddress, StandardCharsets.UTF_8.toString()) }
    val decodedServiceName = remember(serviceName) { URLDecoder.decode(serviceName, StandardCharsets.UTF_8.toString()) }

    val timeSlots = listOf("9:00 AM", "10:00 AM", "11:00 AM", "12:00 PM", "2:00 PM", "3:00 PM", "4:00 PM")

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Agenda una Cita", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBackIosNew, contentDescription = null)
                    }
                },
                actions = { IconButton(onClick = {}) { Icon(Icons.Default.Search, contentDescription = null) } }
            )
        },
        bottomBar = {
            Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Button(
                    onClick = {
                        val dateMillis = datePickerState.selectedDateMillis
                        if (dateMillis != null && selectedTime != null && selectedSpecialist != null) {

                            scope.launch {
                                dao.insertAppointment(
                                    Appointment(
                                        salonName = salonName,
                                        salonAddress = decodedAddress,
                                        date = dateMillis,
                                        time = selectedTime!!,
                                        specialistName = selectedSpecialist!!.name,
                                        serviceName = decodedServiceName,
                                        price = servicePrice,
                                        status = "Active"
                                    )
                                )

                                if (hasNotificationPermission) {
                                    NotificationUtils.showImmediateNotification(
                                        context,
                                        "¡Cita Confirmada!",
                                        "Tu cita para $decodedServiceName en $salonName ha sido agendada."
                                    )

                                    try {
                                        val timeFormat = SimpleDateFormat("h:mm a", Locale.US)
                                        val timeDate = timeFormat.parse(selectedTime!!)

                                        val calendar = Calendar.getInstance().apply {
                                            timeInMillis = dateMillis
                                            val timeCal = Calendar.getInstance().apply { time = timeDate!! }
                                            set(Calendar.HOUR_OF_DAY, timeCal.get(Calendar.HOUR_OF_DAY))
                                            set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE))
                                            set(Calendar.SECOND, 0)
                                        }

                                        val appointmentTime = calendar.timeInMillis
                                        val reminderTime = appointmentTime - 3600000

                                        if (reminderTime > System.currentTimeMillis()) {
                                            NotificationUtils.scheduleNotification(
                                                context,
                                                reminderTime,
                                                "Recordatorio de Cita",
                                                "Tienes una cita en 1 hora en $salonName con ${selectedSpecialist!!.name}."
                                            )
                                        }
                                    } catch (e: Exception) {
                                        e.printStackTrace() // Manejo básico de error de parseo
                                    }
                                }

                                Toast.makeText(context, "¡Cita agendada con éxito!", Toast.LENGTH_SHORT).show()

                                navController.navigate("home") {
                                    popUpTo("home") { inclusive = true }
                                }
                            }

                        } else {
                            Toast.makeText(context, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PurpleStart),
                    shape = RoundedCornerShape(25.dp)
                ) {
                    Text("Confirmar Cita ($servicePrice)", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                val colors = DatePickerDefaults.colors(
                    selectedDayContainerColor = PurpleStart,
                    todayDateBorderColor = PurpleStart,
                    todayContentColor = PurpleStart
                )
                DatePicker(
                    state = datePickerState,
                    colors = colors,
                    title = null,
                    headline = null,
                    showModeToggle = false,
                    modifier = Modifier.padding(8.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Horarios Disponibles", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
            Spacer(modifier = Modifier.height(12.dp))

            val chunkedSlots = timeSlots.chunked(2)
            chunkedSlots.forEach { pair ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    pair.forEach { time ->
                        TimeSlotChip(
                            time = time,
                            isSelected = time == selectedTime,
                            onSelect = { selectedTime = time },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (pair.size == 1) Spacer(modifier = Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Seleccionar Especialista", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp), contentPadding = PaddingValues(horizontal = 4.dp)) {
                items(mockSpecialists) { specialist ->
                    SpecialistSelectionItem(
                        specialist = specialist,
                        isSelected = specialist == selectedSpecialist,
                        onClick = { selectedSpecialist = specialist }
                    )
                }
            }
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}


@Composable
fun TimeSlotChip(
    time: String,
    isSelected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onSelect,
        modifier = modifier.height(45.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, if (isSelected) PurpleStart else Color.LightGray),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (isSelected) PurpleStart.copy(alpha = 0.1f) else Color.Transparent
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = time,
                color = if (isSelected) PurpleStart else Color.Gray,
                fontSize = 14.sp
            )
            if (isSelected) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = PurpleStart,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun SpecialistSelectionItem(
    specialist: Specialist,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(4.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(70.dp)
                .border(
                    width = if (isSelected) 3.dp else 0.dp,
                    color = if (isSelected) PurpleStart else Color.Transparent,
                    shape = CircleShape
                )
                .padding(4.dp)
                .clip(CircleShape)
                .background(specialist.color)
        ) {
            Text(
                text = specialist.name.take(1),
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = specialist.name.split(" ")[0],
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) PurpleStart else Color.Gray
        )
        Row {
            repeat(5) {
                Icon(
                    Icons.Default.Star,
                    contentDescription = null,
                    tint = Color(0xFFFFD700),
                    modifier = Modifier.size(10.dp)
                )
            }
        }
    }
}