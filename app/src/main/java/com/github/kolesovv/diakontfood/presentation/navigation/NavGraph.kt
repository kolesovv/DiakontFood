package com.github.kolesovv.diakontfood.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.kolesovv.diakontfood.presentation.screens.menu.MenuScreen
import com.github.kolesovv.diakontfood.presentation.screens.payment.PaymentScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Menu.route
    ) {
        composable(Screen.Menu.route) {
            MenuScreen(
                onNavigateToPayment = {
                    navController.navigate(Screen.Pay.route)
                },
                onNavigateToHistory = { }
            )
        }
        composable(Screen.Pay.route) {
            PaymentScreen(
                onBackToMenu = {
                    navController.popBackStack()
                }
            )
        }
    }
}

sealed class Screen(val route: String) {

    data object Menu : Screen("menu")
    data object Pay : Screen("pay")
}