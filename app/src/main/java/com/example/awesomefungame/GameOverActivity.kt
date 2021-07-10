package com.example.awesomefungame

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class GameOverActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_over)
        val db = Firebase.firestore
        val name = intent.getStringExtra("playerName")
        val score = intent.getStringExtra("playerScore")
        val lobby = intent.getStringExtra("lobby")
        val playerKey = intent.getStringExtra("playerKey")

        val tvThanks: TextView = findViewById(R.id.tv_thanks)
        tvThanks.text = "Thank you for playing, $name."

        val tvFinalScore: TextView = findViewById(R.id.tv_finalScore)
        tvFinalScore.text = "Your final score was: $score"

        val btEnd: Button = findViewById(R.id.bt_endGame)
        btEnd.setOnClickListener {
            deletePlayerInLobby(db, lobby, playerKey)
            endGame()
        }
    }

    fun endGame() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun deletePlayerInLobby(db: FirebaseFirestore, lobby: String?, playerKey: String?) {
        db.collection(lobby.toString())
            .document(playerKey.toString())
            .delete()
    }
}