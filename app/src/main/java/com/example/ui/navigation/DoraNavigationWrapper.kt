package com.example.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.ui.Alignment
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.*
import com.example.ui.viewmodel.DoraViewModel

@Composable
fun DoraNavigationWrapper(
    viewModel: DoraViewModel,
    modifier: Modifier = Modifier
) {
    var showSplash by remember { mutableStateOf(true) }

    if (showSplash) {
        SplashScreen(
            onSplashFinished = { showSplash = false },
            modifier = Modifier.fillMaxSize()
        )
    } else {
        val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
        val navController = rememberNavController()

        // Redirect or display based on logged-in status
        if (currentUser == null) {
            AuthScreen(viewModel = viewModel, modifier = Modifier.fillMaxSize())
        } else {
        Scaffold(
            bottomBar = {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                NavigationBar(
                    modifier = Modifier.testTag("dora_bottom_nav_bar")
                ) {
                    NavigationBarItem(
                        selected = currentRoute == "home",
                        onClick = {
                            if (currentRoute != "home") {
                                navController.navigate("home") {
                                    popUpTo("home") { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = if (currentRoute == "home") Icons.Default.Home else Icons.Outlined.Home,
                                contentDescription = "Home"
                            )
                        },
                        label = { Text("Home") },
                        modifier = Modifier.testTag("nav_btn_home")
                    )

                    NavigationBarItem(
                        selected = currentRoute == "search",
                        onClick = {
                            if (currentRoute != "search") {
                                navController.navigate("search") {
                                    popUpTo("home") { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = if (currentRoute == "search") Icons.Default.Search else Icons.Outlined.Search,
                                contentDescription = "Search"
                            )
                        },
                        label = { Text("Search") },
                        modifier = Modifier.testTag("nav_btn_search")
                    )

                    // CENTER HIGHLIGHTED BUTTON: Reels
                    NavigationBarItem(
                        selected = currentRoute == "reels",
                        onClick = {
                            if (currentRoute != "reels") {
                                navController.navigate("reels") {
                                    popUpTo("home") { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .background(
                                        brush = androidx.compose.ui.graphics.Brush.linearGradient(
                                            colors = if (currentRoute == "reels")
                                                listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.tertiary)
                                            else
                                                listOf(MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f), MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                                        ),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .padding(6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayCircle,
                                    contentDescription = "Reels",
                                    tint = if (currentRoute == "reels") androidx.compose.ui.graphics.Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                        },
                        label = { Text("Reels", fontWeight = if (currentRoute == "reels") FontWeight.Bold else FontWeight.Normal) },
                        modifier = Modifier.testTag("nav_btn_reels")
                    )

                    NavigationBarItem(
                        selected = currentRoute == "clips",
                        onClick = {
                            if (currentRoute != "clips") {
                                navController.navigate("clips") {
                                    popUpTo("home") { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = if (currentRoute == "clips") Icons.Default.VideoLibrary else Icons.Outlined.VideoLibrary,
                                contentDescription = "Clips"
                            )
                        },
                        label = { Text("Clips") },
                        modifier = Modifier.testTag("nav_btn_clips")
                    )

                    NavigationBarItem(
                        selected = currentRoute == "profile",
                        onClick = {
                            if (currentRoute != "profile") {
                                navController.navigate("profile") {
                                    popUpTo("home") { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = if (currentRoute == "profile") Icons.Default.Person else Icons.Outlined.Person,
                                contentDescription = "Profile"
                            )
                        },
                        label = { Text("Profile") },
                        modifier = Modifier.testTag("nav_btn_profile")
                    )
                }
            },
            modifier = modifier.fillMaxSize()
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "home",
                modifier = Modifier.padding(innerPadding)
            ) {
                composable("home") {
                    HomeScreen(
                        viewModel = viewModel,
                        onNavigateToSearch = { navController.navigate("search") }
                    )
                }
                composable("search") {
                    SearchScreen(viewModel = viewModel)
                }
                composable("reels") {
                    ReelsScreen(viewModel = viewModel)
                }
                composable("clips") {
                    ClipsScreen(viewModel = viewModel)
                }
                composable("profile") {
                    ProfileScreen(viewModel = viewModel)
                }
            }
        }
    }
}
}
