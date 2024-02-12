package com.bitmavrick.simplerick.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import com.bitmavrick.network.KtorClient
import com.bitmavrick.network.models.domain.Character
import com.bitmavrick.simplerick.components.character.CharacterDetailsNamePlateComponent
import com.bitmavrick.simplerick.components.common.DataPoint
import com.bitmavrick.simplerick.components.common.DataPointComponent
import com.bitmavrick.simplerick.components.common.LoadingState
import kotlinx.coroutines.delay

@Composable
fun CharacterDetailsScreen(
    ktorClient: KtorClient,
    characterId: Int
) {
    var character by remember { mutableStateOf<Character?>(null) }

    val characterDataPoints by remember {
        derivedStateOf {
            buildList {
                character?.let { character ->
                    add(DataPoint("Last known location", character.location.name))
                    add(DataPoint("Species", character.species))
                    add(DataPoint("Gender", character.gender.genderString))
                    character.type.takeIf { it.isNotEmpty() } ?.let { type ->
                        add(DataPoint("Type", type))
                    }
                    add(DataPoint("Origin", character.origin.name))
                    add(DataPoint("Episode count", character.episodeIds.size.toString()))
                }
            }
        }
    }

    LaunchedEffect(key1 = Unit) {
        delay(500)
        character = ktorClient.getCharacter(characterId)
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(all = 16.dp)
    ){
        if (character == null) {
            item { LoadingState() }
            return@LazyColumn
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        // Name plate
        item {
            CharacterDetailsNamePlateComponent(
                name = character!!.name,
                status = character!!.status
            )
        }

        item { Spacer(modifier = Modifier.height(8.dp)) }

        // Image
        item {
            SubcomposeAsyncImage(
                model = character!!.imageUrl,
                contentDescription = "Character Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(15.dp)),
                loading = {
                    LoadingState()
                }
            )
        }
        
        // Data points
        items(characterDataPoints){
            Spacer(modifier = Modifier.height(32.dp))
            DataPointComponent(dataPoint = it)
        }
    }
}