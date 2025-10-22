package com.github.kolesovv.diakontfood.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.github.kolesovv.diakontfood.domain.usecase.RegisterOrderUseCase
import com.github.kolesovv.diakontfood.presentation.navigation.NavGraph
import com.github.kolesovv.diakontfood.presentation.ui.theme.DiakontFoodTheme
import dagger.hilt.android.AndroidEntryPoint
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
                NavGraph()
            }
        }
    }
}
