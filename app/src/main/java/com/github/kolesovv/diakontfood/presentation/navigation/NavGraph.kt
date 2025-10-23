package com.github.kolesovv.diakontfood.presentation.navigation

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.kolesovv.diakontfood.presentation.screens.menu.MenuScreen
import com.github.kolesovv.diakontfood.presentation.screens.orders.OrdersScreen
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
                    navController.navigate(Screen.Pay.createRoute(dishIds = it))
                },
                onNavigateToHistory = {
                    navController.navigate(Screen.Orders.route)
                }
            )
        }
        composable(Screen.Pay.route) {
            PaymentScreen(
                dishIds = Screen.Pay.getDishIds(it.arguments),
                onBackToMenu = {
                    navController.popBackStack()
                }
            )
        }
        composable(Screen.Orders.route) {
            OrdersScreen(
                onBackToMenu = {
                    navController.popBackStack()
                }
            )
        }
    }
}

sealed class Screen(val route: String) {

    data object Menu : Screen("menu")
    data object Pay : Screen("pay/{dish_ids}") {

        const val SEPARATOR = ";"

        fun createRoute(dishIds: List<Int>): String {
            val ids = dishIds.joinToString(separator = SEPARATOR)
            return "pay/$ids"
        }

        fun getDishIds(arguments: Bundle?): List<Int> {
            val dishIdsString = arguments?.getString("dish_ids") ?: ""
            return dishIdsString.split(SEPARATOR).map { it.toInt() }
        }
    }

    data object Orders : Screen("orders")
}