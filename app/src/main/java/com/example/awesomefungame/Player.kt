package com.example.awesomefungame
// Player class that controls lives, score, difficulty, and related streaks
class Player {
    var name: String = ""
    var lives: Int = 3
    var correctStreak: Int = 0
    var streakCount: Int = 0
    var highestStreakCount: Int = 0
    var score: Int = 0
    var difficultyStreak: Int = 0
    var difficulty: Int = 1
    var playerKey: String = ""
    var lobby: String = ""
}