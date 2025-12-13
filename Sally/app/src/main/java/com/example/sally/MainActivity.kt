package com.example.sally

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainApp()
        }
    }
}

@Composable
fun MainApp() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val mainRoutes = listOf("home", "favorites", "chats", "profile")

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val isMainScreen = currentRoute in mainRoutes

    val items = listOf(
        Triple("home", "Inicio", Icons.Default.Home),
        Triple("favorites", "Favoritos", Icons.Default.FavoriteBorder),
        Triple("chats", "Chats", Icons.Outlined.ChatBubbleOutline),
        Triple("profile", "Perfil", Icons.Outlined.Person)
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = isMainScreen,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(12.dp))
                NavigationDrawerItem(
                    label = { Text("Configuración") },
                    selected = false,
                    onClick = { scope.launch { drawerState.close() } }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                if (isMainScreen) {
                    SalyTopBar(onMenuClick = { scope.launch { drawerState.open() } })
                }
            },
            bottomBar = {
                if (isMainScreen) {
                    NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
                        items.forEach { (route, label, icon) ->
                            NavigationBarItem(
                                icon = { Icon(icon, contentDescription = label) },
                                label = { Text(label) },
                                selected = currentRoute == route,
                                onClick = {
                                    navController.navigate(route) {
                                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = PurpleStart,
                                    selectedTextColor = PurpleStart,
                                    indicatorColor = Color.Transparent
                                )
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "home",
                modifier = Modifier.padding(innerPadding)
            ) {
                composable("home") { HomeScreen(navController) }
                composable("favorites") { FavoritesScreen(navController) }
                composable("chats") { ChatsScreen(navController) }
                composable("profile") { ProfileScreen(navController) }

                composable(
                    route = "salon_profile/{salonId}",
                    arguments = listOf(navArgument("salonId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val salonId = backStackEntry.arguments?.getInt("salonId") ?: 0
                    val selectedSalon = mockSalons.find { it.id == salonId } ?: mockSalons[0]
                    val initialState = if (selectedSalon.isClosed) ClosedState() else OpenState()

                    SalonProfileScreen(
                        navController = navController,
                        initialState = initialState,
                        salonData = selectedSalon
                    )
                }
                composable(
                    route = "appointments/{tabIndex}",
                    arguments = listOf(navArgument("tabIndex") { type = NavType.IntType })
                ) { backStackEntry ->
                    val tabIndex = backStackEntry.arguments?.getInt("tabIndex") ?: 0
                    AppointmentsScreen(navController = navController, initialTab = tabIndex)
                }
                composable(
                    route = "booking/{salonName}/{salonAddress}/{serviceName}/{servicePrice}",
                    arguments = listOf(
                        navArgument("salonName") { type = NavType.StringType },
                        navArgument("salonAddress") { type = NavType.StringType },
                        navArgument("serviceName") { type = NavType.StringType },
                        navArgument("servicePrice") { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    val salonName = backStackEntry.arguments?.getString("salonName") ?: ""
                    val salonAddress = backStackEntry.arguments?.getString("salonAddress") ?: ""
                    val serviceName = backStackEntry.arguments?.getString("serviceName") ?: ""
                    val servicePrice = backStackEntry.arguments?.getString("servicePrice") ?: ""

                    BookingScreen(
                        navController = navController,
                        salonName = salonName,
                        salonAddress = salonAddress,
                        serviceName = serviceName,
                        servicePrice = servicePrice
                    )
                }
                composable(
                    route = "map/{salonName}/{lat}/{lng}",
                    arguments = listOf(
                        navArgument("salonName") { type = NavType.StringType },
                        navArgument("lat") { type = NavType.FloatType },
                        navArgument("lng") { type = NavType.FloatType }
                    )
                ) { backStackEntry ->
                    val name = backStackEntry.arguments?.getString("salonName") ?: ""
                    val lat = backStackEntry.arguments?.getFloat("lat") ?: 0f
                    val lng = backStackEntry.arguments?.getFloat("lng") ?: 0f

                    MapScreen(
                        navController = navController,
                        salonName = name,
                        targetLocation = LatLng(lat.toDouble(), lng.toDouble())
                    )
                }
            }
        }
    }
}