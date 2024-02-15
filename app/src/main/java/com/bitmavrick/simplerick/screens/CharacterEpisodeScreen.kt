package com.bitmavrick.simplerick.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bitmavrick.network.KtorClient
import com.bitmavrick.network.models.domain.Character
import com.bitmavrick.network.models.domain.Episode
import com.bitmavrick.simplerick.components.common.CharacterImage
import com.bitmavrick.simplerick.components.common.CharacterNameComponent
import com.bitmavrick.simplerick.components.common.DataPoint
import com.bitmavrick.simplerick.components.common.DataPointComponent
import com.bitmavrick.simplerick.components.common.LoadingState
import com.bitmavrick.simplerick.components.episode.EpisodeRowComponent
import com.bitmavrick.simplerick.ui.theme.RickPrimary
import com.bitmavrick.simplerick.ui.theme.RickTextPrimary
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


@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MainScreen(
    character: Character,
    episodes: List<Episode>
){
    val episodeBySeasonMap = episodes.groupBy { it.seasonNumber }

    LazyColumn(
        contentPadding = PaddingValues(all = 16.dp)
    ){
        item{ CharacterNameComponent(name = character.name) }
        item{ Spacer(modifier = Modifier.height(16.dp)) }
        item {
            LazyRow {
                episodeBySeasonMap.forEach { mapEntry ->
                    val title = "Season ${mapEntry.key}"
                    val description = "${mapEntry.value.size} ep"

                    item {
                        DataPointComponent(dataPoint = DataPoint(title, description))
                        Spacer(modifier = Modifier.width(16.dp))
                    }
                }
            }
        }
        item { Spacer(modifier = Modifier.height(16.dp)) }
        item{ CharacterImage(imageUrl = character.imageUrl) }
        item { Spacer(modifier = Modifier.height(32.dp)) }

        // Episodes
        episodeBySeasonMap.forEach{mapEntry ->
            stickyHeader { SeasonHeader(seasonNumber = mapEntry.key) }
            item { Spacer(modifier = Modifier.height(16.dp)) }
            items(mapEntry.value) {episode ->
                EpisodeRowComponent(episode = episode)
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun SeasonHeader(seasonNumber: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = RickPrimary)
            .padding(top = 8.dp, bottom = 16.dp)
    ){
        Text(
            text = "Season $seasonNumber",
            color = RickTextPrimary,
            fontSize = 32.sp,
            lineHeight = 32.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = RickTextPrimary,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(vertical = 8.dp)
        )
    }
}