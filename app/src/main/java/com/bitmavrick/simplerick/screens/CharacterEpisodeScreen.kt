package com.bitmavrick.simplerick.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bitmavrick.network.KtorClient
import com.bitmavrick.network.models.domain.Character
import com.bitmavrick.network.models.domain.Episode
import com.bitmavrick.simplerick.components.common.CharacterImage
import com.bitmavrick.simplerick.components.common.CharacterNameComponent
import com.bitmavrick.simplerick.components.common.LoadingState
import com.bitmavrick.simplerick.components.episode.EpisodeRowComponent
import kotlinx.coroutines.launch

@Composable
fun CharacterEpisodeScreen(
    characterId: Int,
    ktorClient: KtorClient
) {
    var characterState by remember { mutableStateOf<Character?>(null) }
    var episodesState by remember { mutableStateOf<List<Episode>>(emptyList()) }

    LaunchedEffect(key1 = Unit, block = {
        ktorClient.getCharacter(characterId).onSuccess {character ->
            characterState = character
            launch {
                ktorClient.getEpisodes(character.episodeIds).onSuccess { episodes->
                    episodesState = episodes
                }.onFailure {
                    // TODO: Error will handle later
                }
            }

        }.onFailure {
            // TODO: Error will handle later
        }
    })

    characterState?.let {character ->
        MainScreen(
            character = character,
            episodes = episodesState
        )
    } ?: LoadingState()
}


@Composable
private fun MainScreen(
    character: Character,
    episodes: List<Episode>
){
    LazyColumn(
        contentPadding = PaddingValues(all = 16.dp)
    ){
        item{ CharacterNameComponent(name = character.name) }
        item{ Spacer(modifier = Modifier.height(16.dp)) }
        item{ CharacterImage(imageUrl = character.imageUrl) }

        items(episodes){
            EpisodeRowComponent(episode = it)
        }
    }
}