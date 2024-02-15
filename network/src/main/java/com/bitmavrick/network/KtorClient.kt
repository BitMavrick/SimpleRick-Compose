package com.bitmavrick.network

import com.bitmavrick.network.models.domain.Character
import com.bitmavrick.network.models.domain.Episode
import com.bitmavrick.network.models.remote.RemoteCharacter
import com.bitmavrick.network.models.remote.RemoteEpisode
import com.bitmavrick.network.models.remote.toDomainCharacter
import com.bitmavrick.network.models.remote.toDomainEpisode
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import java.lang.Exception

class KtorClient {
    private val client = HttpClient(OkHttp) {
        defaultRequest { url("https://rickandmortyapi.com/api/") }

        install(Logging) {
            logger = Logger.SIMPLE
        }

        install(ContentNegotiation){
            json(Json{
                ignoreUnknownKeys = true
            })
        }
    }

    private var characterCache = mutableMapOf<Int, Character>()

    suspend fun getCharacter(id: Int) : ApiOperation<Character> {
        // Cache state
        characterCache[id]?.let {
            return ApiOperation.Success(it)
        }

        // Actual network call
        return safeApiCall {
            client.get("character/$id")
                .body<RemoteCharacter>()
                .toDomainCharacter()
                .also { characterCache[id] = it }
        }
    }

    suspend fun getEpisodes(episodeIds: List<Int>) : ApiOperation<List<Episode>> {

        return if (episodeIds.size == 1) {
            getEpisode(episodeIds[0]).mapSuccess {
                listOf(it)
            }
        } else {
            val idsCommaSeparator = episodeIds.joinToString(separator = ",")

            safeApiCall {
                client.get("episode/$idsCommaSeparator")
                    .body<List<RemoteEpisode>>()
                    .map{ it.toDomainEpisode() }
            }
        }
    }

    suspend fun getEpisode(episodeId: Int) : ApiOperation<Episode> {
        return safeApiCall {
            client.get("episode/$episodeId")
                .body<RemoteEpisode>()
                .toDomainEpisode()
        }
    }

    private inline fun <T> safeApiCall(apiCall:() -> T): ApiOperation<T> {
        return try {
            ApiOperation.Success(data = apiCall())
        } catch (e: Exception) {
            ApiOperation.Failure(exception = e)
        }
    }
}

sealed interface ApiOperation<T> {
    data class Success<T> (val data: T) : ApiOperation<T>
    data class Failure<T> (val exception: Exception) : ApiOperation<T>

    fun <R> mapSuccess(transform: (T) -> R): ApiOperation<R> {
        return when (this) {
            is Success -> Success(transform(data))
            is Failure -> Failure(exception)
        }
    }

    fun onSuccess(block: (T) -> Unit) : ApiOperation<T> {
        if (this is Success) block(data)
        return this
    }

    fun onFailure(block: (Exception) -> Unit) : ApiOperation<T> {
        if (this is Failure) block(exception)
        return this
    }
}

