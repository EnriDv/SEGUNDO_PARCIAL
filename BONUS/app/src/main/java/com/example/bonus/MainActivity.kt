package com.example.bonus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel = ViewModelProvider(this)[FibonacciViewModel::class.java]


    }
}

class FibonacciViewModel : ViewModel() {

    private val _fibonacciState = MutableStateFlow("")
    val fibonacciState: StateFlow<String> = _fibonacciState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _snackbarChannel = Channel<String>()
    val snackbarEvents = _snackbarChannel.receiveAsFlow()

    fun generarSerie(inputText: String) {
        val n = inputText.toIntOrNull()

        if (n == null || n <= 0) {
            sendError("Por favor ingresa un número entero mayor a 0")
            return
        }

        if (n > 35) {
            sendError("N es muy alto para cálculo recursivo, intenta < 35")
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _fibonacciState.value = ""

            val resultado = withContext(Dispatchers.Default) {
                (0 until n).map { i ->
                    fibonacciRecursivo(i)
                }.joinToString(", ")
            }

            _fibonacciState.value = resultado
            _isLoading.value = false
        }
    }

    private fun sendError(message: String) {
        viewModelScope.launch {
            _snackbarChannel.send(message)
        }
    }

    private fun fibonacciRecursivo(n: Int): Long {
        if (n <= 1) return n.toLong()
        return fibonacciRecursivo(n - 1) + fibonacciRecursivo(n - 2)
    }
}

