package com.example.mybudgetapp

data class LobbyPlayers(
    val players: MutableList<PeoplePlaying> = mutableListOf(),
)

data class PeoplePlaying(
    val player: String,
    val score: Double,
)
