package com.example.mybudgetapp

data class LobbyData(
    var lobby: Long = 0L,
    val players: MutableList<String> = mutableListOf(),
    val score: MutableList<String> = mutableListOf(),
)

data class LobbyDataSample(
    val player: String,
    val score: Double
)
