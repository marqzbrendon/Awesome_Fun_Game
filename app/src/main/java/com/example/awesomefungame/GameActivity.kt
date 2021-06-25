package com.example.awesomefungame

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.mybudgetapp.LobbyData
import com.example.mybudgetapp.Players
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class GameActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        // Initiate player instance
        val playerName = intent.getStringExtra("name")
        val lobby = intent.getStringExtra("lobby")
        val player = playerName?.let { Players(it,0.0) }

        // This creates a random sequence that will be used as ID for the doc in the database
        val charPool : List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        val playerKey = (1..10)
            .map { _ -> kotlin.random.Random.nextInt(0, charPool.size) }
            .map(charPool::get)
            .joinToString("");

        // Lobby data
        val lobbyPlayers = LobbyData()

        // Initiate database instance
        val db = Firebase.firestore
        val database = AppDatabase(GameActivity())

        // Add new player to Lobby database
        database.addPlayerInLobby(db, player, lobby, playerKey)

        // Display ListView of players in the lobby
        val adapter: ArrayAdapter<Any> = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line, arrayListOf("Retrieving data...") as List<Any>
        )
        adapter.notifyDataSetChanged()

        // Getting all the lobby data from the database
        database.retrieveAllDocuments(db, lobby, lobbyPlayers, adapter)

        // Display test
        val tvMessage : TextView = findViewById(R.id.tv_message)
        tvMessage.text = "Player: $playerName. Lobby: $lobby."

        // Remove player
        val btEnd: Button = findViewById(R.id.bt_end)
        btEnd.setOnClickListener {
            database.deletePlayerInLobby(db, playerKey, lobby)
            finish()
        }

    }

    fun setUpScoreData(lobbyData: LobbyData, adapter: ArrayAdapter<Any>) {

    }
}