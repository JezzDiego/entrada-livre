package com.example.entradalivres

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.navigation.compose.rememberNavController
import com.example.entradalivres.presentation.navigation.EntradaLivreNavHost
import com.example.entradalivres.presentation.ui.theme.EntradaLivreTheme

class MainActivity : ComponentActivity() {
    private val container by lazy { (application as EntradaLivreApp).container }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isDark by container.preferences.isDarkMode.collectAsState(initial = false)
            val isLargeText by container.preferences.isLargeText.collectAsState(initial = false)

            val density = LocalDensity.current
            val scaledDensity = Density(
                density = density.density,
                fontScale = density.fontScale * if (isLargeText) LARGE_TEXT_SCALE else 1f,
            )

            CompositionLocalProvider(LocalDensity provides scaledDensity) {
                EntradaLivreTheme(darkTheme = isDark) {
                    Surface(
                        color = MaterialTheme.colorScheme.background,
                        contentColor = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        val navController = rememberNavController()
                        EntradaLivreNavHost(navController, container = container)
                    }
                }
            }
        }
    }

    private companion object {
        const val LARGE_TEXT_SCALE = 1.25f
    }
}
