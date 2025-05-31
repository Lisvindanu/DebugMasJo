package com.example.kostkita

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.kostkita.presentation.navigation.KostKitaNavigation
import com.example.kostkita.presentation.theme.KostKitaTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KostKitaTheme {
                KostKitaNavigation()
            }
        }
    }
}