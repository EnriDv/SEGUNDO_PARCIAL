package com.example.ejercicio1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ejercicio1.ui.theme.EJERCICIO1Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EJERCICIO1Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    UserCard(nombre = "Yo", fotoUrl = "url", onFollowClick = {})
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Composable
fun UserCard (nombre: String, fotoUrl: String, onFollowClick: () -> Unit) {
    Column ( modifier = Modifier.padding(15.dp)){
        Row (modifier = Modifier.padding(5.dp).innerPadd) {
            Text( text = "imagen")
            Text(text = nombre)
            Text(text = "Estado")
        }

        Button(onClick = { /*TODO*/ }) { Text(text = "Seguir") }

    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    EJERCICIO1Theme {
        Greeting("Android")
    }
}