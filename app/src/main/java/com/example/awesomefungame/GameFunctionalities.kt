package com.example.awesomefungame

import android.util.Log

class GameFunctionalities(private val gameActivity: GameActivity) {
    // Changes the score, difficulty, and related streaks when a correct answer is inputted
    fun correctAnswer(player: Player) {
        player.score += 10
        player.score += player.correctStreak
        player.correctStreak += 5
        player.streakCount += 1
        if (player.streakCount > player.highestStreakCount) {
            player.highestStreakCount = player.streakCount
        }
        player.difficultyStreak += 1
        // Increases the difficulty every three levels (when the difficulty streak is a multiple of three)
        if (player.difficultyStreak % 3 == 0) {
            if (player.difficulty < 6) {
                player.difficulty += 1
                levelUp(player)
            }
        }
        gameActivity.updateScore()
        runGame(player.difficulty)
    }

    // Changes the lives, difficulty, and related streaks when a incorrect answer is inputted
    fun incorrectAnswer(player: Player) {
        if (player.lives == 1) {
            gameActivity.gameOver()
        } else {
            player.lives -= 1
            player.correctStreak = 0
            player.difficultyStreak = 0
            player.streakCount = 0
            gameActivity.updateScore()
            runGame(player.difficulty)
        }
    }

    // Lets the player know they've moved to the next level
    private fun levelUp(player: Player) {
        println("Congrats! You have reached level ${player.difficulty}")
        println()
    }

    // the basic game function that will run each time we cycle through our do-while loop
    fun runGame(d: Int) {
        // generates x and y values and the solution based on the difficulty
        var (x, y, s) = when (d) {
            1 -> generateNums(1)
            2 -> generateNums(2)
            3 -> generateNums(3)
            4 -> generateNums(4)
            5 -> generateNums(5)
            else -> generateNums(6)
        }
        // determines the operator each difficulty uses
        val op: String = when (d) {
            1 -> "+"
            2 -> "+"
            3 -> "-"
            4 -> "-"
            5 -> "*"
            else -> "/"
        }
        // if it's a subtraction level and the answer would be a negative number, switches the x and y values and recalculates the answer
        if (x < y && op == "-") {
            x = y.also { y = x }
            s = x - y
        }
        gameActivity.displayValues(x, y, op)
        gameActivity.correctAnswer = s.toString()
    }

    //generates the x and y values and the solution based on the difficulty level
    private fun generateNums(d: Int): Triple<Int, Int, Int> {
        if (d != 6) {
            //generates the numbers for all the difficulty levels except 6
            val x: Int = when (d) {
                1, 3 -> (0..10).random()
                2, 4 -> (10..100).random()
                else -> (0..12).random()
            }
            val y: Int = when (d) {
                1, 3 -> (0..10).random()
                2, 4 -> (10..100).random()
                else -> (0..12).random()
            }
            val z: Int = when (d) {
                1, 2 -> x + y
                3, 4 -> x - y
                else -> x * y
            }
            return Triple(x, y, z)
        } else { // generates the values for level 6 (I needed to calculate x from y and s which wouldn't be defined if I hadn't defined it separately).
            val y: Int = (1..10).random()
            val z: Int = (1..20).random()
            val x: Int = y * z
            Log.d("solution", "$z")
            return Triple(x, y, z)
        }
    }

    // checks if the answer equals the solution and returns true or false
    fun checkAnswer(a: String, b: String): Boolean {
        return a == b
    }
}
