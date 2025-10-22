package com.github.kolesovv.diakontfood.presentation

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.github.kolesovv.diakontfood.presentation.screen.MenuScreen
import com.github.kolesovv.diakontfood.presentation.ui.theme.DiakontFoodTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DiakontFoodTheme {
                MenuScreen(
                    onNavigateToPayment = {
                        Toast.makeText(
                            this,
                            "Скоро здесь будет экран оплаты",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    onNavigateToHistory = {
                        Toast.makeText(
                            this,
                            "Скоро здесь будет экран заказов",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
            }
        }
    }
}
