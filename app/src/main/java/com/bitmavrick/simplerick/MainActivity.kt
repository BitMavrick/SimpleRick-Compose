package com.bitmavrick.simplerick

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.bitmavrick.network.KtorClient
import com.bitmavrick.network.models.domain.Character
import com.bitmavrick.simplerick.ui.theme.SimpleRickTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {

    private val ktorClient = KtorClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            var character by remember { mutableStateOf<Character?>(null) }

            LaunchedEffect(key1 = Unit, block = {
                delay(3000)
                character = ktorClient.getCharacter(55)
            })
            
            SimpleRickTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Text(text = character?.name ?: "Character Loading ...")
                }
            }
        }
    }
}

// TODO : Next Jetpack compose + Coil (Saving)