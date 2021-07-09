package com.example.awesomefungame

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.mybudgetapp.LobbyPlayers
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class GameActivity : AppCompatActivity() {
    // This creates a random sequence that will be used as ID for the doc in the database
    private val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
    private val playerKey = (1..15)
        .map { _ -> kotlin.random.Random.nextInt(0, charPool.size) }
        .map(charPool::get)
        .joinToString("");

    private lateinit var lobby: String

    private val db = Firebase.firestore
    private lateinit var database: AppDatabase

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        // Initiate player instance
        val playerName = intent.getStringExtra("name")!!
        val player = Player()
        player.name = playerName

        lobby = intent.getStringExtra("lobby").toString()

        // Lobby data
        val lobbyPlayers = LobbyPlayers()

        // Initiate database instance
        database = AppDatabase(this)

        // Add new player to Lobby database
        database.addPlayerInLobby(db, player, lobby, playerKey)

        // Getting all the lobby data from the database
        val adapter: ArrayAdapter<Any> = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            arrayListOf("Retrieving data...") as List<Any>
        )
        adapter.notifyDataSetChanged()
        val lvPlayers: ListView = findViewById(R.id.lv_players)
        lvPlayers.adapter = adapter
        database.retrieveAllDocuments(db, lobby, lobbyPlayers, adapter, lvPlayers)

        // Display test
        val tv_player: TextView = findViewById(R.id.tv_player)
        tv_player.text = player.name
        val tv_score: TextView = findViewById(R.id.tv_score)
        tv_score.text = "Score: ${player.score}"

        // Remove player
        val btEnd: Button = findViewById(R.id.bt_end)
        btEnd.setOnClickListener {
            database.deletePlayerInLobby(db, playerKey, lobby)
            finish()
        }

    }

    // This function is called whenever there is a change to the database, and it updates the adapter that
    // shows the people in the lobby.
    fun setUpScoreData(lobbyData: LobbyPlayers, adapter: ArrayAdapter<Any>, lvPlayers: ListView) {
        adapter.clear()
        lobbyData.players.map { x ->
            adapter.add("${x.player} - ${x.score}")
            adapter.notifyDataSetChanged()
        }
        lvPlayers.adapter = adapter
    }

    // Remove player from the database if they
    override fun onDestroy() {
        super.onDestroy()
        Log.d("awesomeFunGame","func called onDestroy")
        database.deletePlayerInLobby(db, playerKey, lobby)
    }
}