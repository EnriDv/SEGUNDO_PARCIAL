package com.example.ejercicio2

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver

data class PermissionData(
    val permission: String,
    val title: String
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PermissionsScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun PermissionsScreen(modifier: Modifier = Modifier) {
    val permissionsList = remember {
        mutableStateListOf(
            PermissionData(Manifest.permission.ACCESS_FINE_LOCATION, "Ubicación Exacta"),
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Gestor de Permisos", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(10.dp))

        permissionsList.forEach { permissionData ->
            PermissionItem(
                permission = permissionData.permission,
                title = permissionData.title
            )
        }
    }
}

@Composable
fun PermissionItem(
    permission: String,
    title: String
) {
    val context = LocalContext.current
    val activity = context as? Activity

    var statusText by remember { mutableStateOf("Comprobando...") }
    var statusColor by remember { mutableStateOf(Color.Gray) }
    var isPermanentlyDenied by remember { mutableStateOf(false) }
    var isGranted by remember { mutableStateOf(false) }

    fun checkPermissionStatus() {
        val result = ContextCompat.checkSelfPermission(context, permission)
        isGranted = result == PackageManager.PERMISSION_GRANTED

        if (isGranted) {
            statusText = "Autorizado"
            statusColor = Color(0xFF4CAF50)
            isPermanentlyDenied = false
        } else {
            statusText = "Pendiente / Denegado"
            statusColor = Color(0xFFFF9800)
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                checkPermissionStatus()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { wasGranted ->
        if (wasGranted) {
            statusText = "¡Concedido!"
            statusColor = Color(0xFF4CAF50)
            isGranted = true
            isPermanentlyDenied = false
        } else {
            val shouldShowRationale = activity?.let {
                ActivityCompat.shouldShowRequestPermissionRationale(it, permission)
            } ?: false

            if (!shouldShowRationale) {
                isPermanentlyDenied = true
                statusText = "Denegado permanentemente"
                statusColor = Color.Red
            } else {
                statusText = "Denegado (Puedes intentar de nuevo)"
                statusColor = Color(0xFFFF9800)
            }
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(
                    text = if(isGranted) "✔" else "✘",
                    color = statusColor,
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "Estado: $statusText", fontSize = 14.sp, color = statusColor)

            Spacer(modifier = Modifier.height(12.dp))

            if (!isGranted) {
                if (isPermanentlyDenied) {
                    Button(
                        onClick = { openAppSettings(context) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Abrir Configuración")
                    }
                    Text(
                        text = "Has bloqueado este permiso. Debes activarlo manualmente.",
                        fontSize = 11.sp,
                        lineHeight = 14.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                } else {
                    Button(
                        onClick = { launcher.launch(permission) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Solicitar Permiso")
                    }
                }
            } else {
                OutlinedButton(
                    onClick = {  },
                    enabled = false,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Permiso Activo")
                }
            }
        }
    }
}

fun openAppSettings(context: Context) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", context.packageName, null)
    }
    context.startActivity(intent)
}