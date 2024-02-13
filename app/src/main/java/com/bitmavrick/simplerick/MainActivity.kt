package com.bitmavrick.simplerick

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.bitmavrick.network.KtorClient
import com.bitmavrick.simplerick.screens.CharacterDetailsScreen
import com.bitmavrick.simplerick.ui.theme.RickPrimary
import com.bitmavrick.simplerick.ui.theme.SimpleRickTheme

class MainActivity : ComponentActivity() {

    private val ktorClient = KtorClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SimpleRickTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = RickPrimary
                ) {
                    CharacterDetailsScreen(
                        ktorClient = ktorClient,
                        characterId = 55
                    )
                }
            }
        }
    }

    // TODO: On next --> How to handle API errors
}