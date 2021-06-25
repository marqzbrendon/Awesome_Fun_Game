package com.example.mybudgetapp

data class LobbyData(
    val players: MutableList<Players> = mutableListOf(),
)

data class Players(
    val player: String,
    val score: Double,
)
