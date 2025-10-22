package com.github.kolesovv.diakontfood.presentation

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.github.kolesovv.diakontfood.domain.usecase.RegisterOrderUseCase
import com.github.kolesovv.diakontfood.presentation.screens.menu.MenuScreen
import com.github.kolesovv.diakontfood.presentation.screens.payment.PaymentScreen
import com.github.kolesovv.diakontfood.presentation.ui.theme.DiakontFoodTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var registerOrderUseCase: RegisterOrderUseCase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DiakontFoodTheme {
                PaymentScreen(
                    onBackToMenu = {
                        Toast.makeText(
                            this,
                            "Скоро будем можно вернуться назад",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
                /*MenuScreen(
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
                )*/
            }
        }
    }
}
