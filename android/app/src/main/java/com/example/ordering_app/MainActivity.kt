package com.example.ordering_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import java.text.NumberFormat
import java.util.Locale

import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlinx.coroutines.delay

// ---------- Models ----------
data class CartItem(
    val id: String,
    val name: String,
    val priceCents: Int,
    val qty: Int
)

private fun money(cents: Int): String {
    val nf = NumberFormat.getCurrencyInstance(Locale.US)
    return nf.format(cents / 100.0)
}

// ---------- Routes ----------
sealed class Route(val path: String) {
    data object MainMenu : Route("main_menu")
    data object MyCart : Route("my_cart")
    data object ThankYou : Route("thank_you")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                val nav = rememberNavController()

                // Fake cart state for now (next steps: real shared cart)
                val cartItems = remember {
                    listOf(
                        CartItem("1", "Spicy Tonkotsu Ramen", 1499, 1),
                        CartItem("2", "Gyoza (6 pcs)", 799, 2),
                        CartItem("3", "Iced Green Tea", 399, 1),
                    )
                }

                NavHost(
                    navController = nav,
                    startDestination = Route.MainMenu.path
                ) {
                    composable(Route.MainMenu.path) {
                        MainMenuScreen(
                            onOpenCart = { nav.navigate(Route.MyCart.path) }
                        )
                    }
                    composable(Route.MyCart.path) {
                        MyCartScreen(
                            items = cartItems,
                            onBack = { nav.popBackStack() },
                            onPay = { nav.navigate(Route.ThankYou.path) }
                        )
                    }
                    composable(Route.ThankYou.path) {
                        ThankYouScreen(
                            onGoHome = {
                                nav.navigate(Route.MainMenu.path) {
                                    popUpTo(Route.MainMenu.path) { inclusive = true }
                                    launchSingleTop = true
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MainMenuScreen(onOpenCart: () -> Unit) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text("Main Menu", fontSize = 28.sp)
            Button(onClick = onOpenCart) { Text("Open Cart") }
            Text("Step 3: My Cart shows items + subtotal.")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyCartScreen(
    items: List<CartItem>,
    onBack: () -> Unit,
    onPay: () -> Unit
) {
    val subtotalCents = remember(items) { items.sumOf { it.priceCents * it.qty } }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Cart") },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("Back") }
                }
            )
        },
        bottomBar = {
            Surface(tonalElevation = 4.dp) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Subtotal", fontWeight = FontWeight.SemiBold)
                        Text(money(subtotalCents), fontWeight = FontWeight.SemiBold)
                    }

                    Spacer(Modifier.height(10.dp))

                    Button(
                        onClick = onPay,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = items.isNotEmpty()
                    ) {
                        Text("Pay")
                    }
                }
            }
        }
    ) { padding ->
        if (items.isEmpty()) {
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Your cart is empty.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(items, key = { it.id }) { item ->
                    CartRow(item)
                }
            }
        }
    }
}

@Composable
fun CartRow(item: CartItem) {
    val lineTotal = item.priceCents * item.qty

    Surface(
        shape = RoundedCornerShape(14.dp),
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(item.name, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                Spacer(Modifier.height(3.dp))
                Text(
                    "${money(item.priceCents)} each",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Surface(shape = RoundedCornerShape(10.dp), tonalElevation = 1.dp) {
                Text(
                    "x${item.qty}",
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.width(12.dp))
            Text(money(lineTotal), fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun ThankYouScreen(onGoHome: () -> Unit) {
    // Auto-return after ~5 seconds
    LaunchedEffect(Unit) {
        delay(5_000)
        onGoHome()
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BowingPersonDrawing(modifier = Modifier.size(220.dp))

            Spacer(Modifier.height(16.dp))

            Text("Thank you!", fontSize = 34.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(Modifier.height(6.dp))
            Text(
                "Your order has been placed.",
                style = MaterialTheme.typography.bodyMedium
            )

            // Optional: keep a manual button for testing (you can remove later)
            Spacer(Modifier.height(16.dp))
            OutlinedButton(onClick = onGoHome) { Text("Back to Main Menu") }
        }
    }
}

@Composable
fun BowingPersonDrawing(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val ink = Color(0xFF2E2E2E)

        // Head
        val headR = w * 0.12f
        drawCircle(
            color = ink,
            radius = headR,
            center = Offset(w * 0.58f, h * 0.32f)
        )

        // Body (bowing arc)
        drawArc(
            color = ink,
            startAngle = 200f,
            sweepAngle = 120f,
            useCenter = false,
            topLeft = Offset(w * 0.22f, h * 0.20f),
            size = Size(w * 0.54f, h * 0.62f),
            style = Stroke(width = w * 0.06f, cap = StrokeCap.Round)
        )

        // Arm
        drawLine(
            color = ink,
            start = Offset(w * 0.48f, h * 0.52f),
            end = Offset(w * 0.30f, h * 0.64f),
            strokeWidth = w * 0.05f,
            cap = StrokeCap.Round
        )

        // Leg / base
        drawLine(
            color = ink,
            start = Offset(w * 0.34f, h * 0.70f),
            end = Offset(w * 0.66f, h * 0.76f),
            strokeWidth = w * 0.06f,
            cap = StrokeCap.Round
        )

        // Mat
        drawRoundRect(
            color = Color(0xFFE0E0EA),
            topLeft = Offset(w * 0.22f, h * 0.82f),
            size = Size(w * 0.56f, h * 0.08f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.04f, w * 0.04f)
        )
    }
}


