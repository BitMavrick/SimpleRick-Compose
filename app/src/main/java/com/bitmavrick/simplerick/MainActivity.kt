package com.bitmavrick.simplerick

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.bitmavrick.network.KtorClient
import com.bitmavrick.simplerick.screens.CharacterDetailsScreen
import com.bitmavrick.simplerick.screens.CharacterEpisodeScreen
import com.bitmavrick.simplerick.ui.theme.RickPrimary
import com.bitmavrick.simplerick.ui.theme.SimpleRickTheme

class MainActivity : ComponentActivity() {

    private val ktorClient = KtorClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            val navController = rememberNavController()

            SimpleRickTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = RickPrimary
                ) {
                    NavHost(navController = navController, startDestination = "character_details"){
                        composable("character_details") {
                            CharacterDetailsScreen(
                                ktorClient = ktorClient,
                                characterId = 114
                            ){
                                navController.navigate("character_episodes/$it")
                            }
                        }

                        composable(
                            route = "character_episodes/{characterId}",
                            arguments = listOf(navArgument("characterId"){type = NavType.IntType})
                        ) { backStackEntry->
                            val characterId: Int = backStackEntry.arguments?.getInt("characterId") ?: -1
                            CharacterEpisodeScreen(
                                characterId = characterId,
                                ktorClient = ktorClient
                            )
                        }
                    }
                }
            }
        }
    }
    // TODO: Next-> Add horizontal scroll
}

