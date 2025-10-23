package com.github.kolesovv.diakontfood.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.github.kolesovv.diakontfood.presentation.navigation.NavGraph
import com.github.kolesovv.diakontfood.presentation.ui.theme.DiakontFoodTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

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
