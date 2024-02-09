package com.bitmavrick.network.models.domain

import kotlinx.serialization.Serializable

@Serializable
data class Character(
    val id: Int,
    val name: String,
    val origin: Origin
){
    @Serializable
    data class Origin(
        val name: String,
    )
}