package com.example.orderingapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import com.example.orderingapp.ui.screens.CartScreen
import com.example.orderingapp.ui.screens.ConfirmationScreen
import com.example.orderingapp.ui.theme.Ordering_appTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            Ordering_appTheme {
                var currentScreen by remember { mutableStateOf("cart") }

                when (currentScreen) {
                    "cart" -> CartScreen(
                        onPlaceOrder = { currentScreen = "confirm" }
                    )

                    "confirm" -> ConfirmationScreen(
                        orderNumber = "A1024",
                        onBackToMenu = { currentScreen = "cart" }
                    )
                }
            }
        }
    }
}

