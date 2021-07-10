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
        .map { kotlin.random.Random.nextInt(0, charPool.size) }
        .map(charPool::get)
        .joinToString("")
    private lateinit var lobby: String
    lateinit var correctAnswer: String
    private val db = Firebase.firestore
    private lateinit var database: AppDatabase
    private val player = Player()
    lateinit var tvScore: TextView

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        // Initiate player instance
        val playerName = intent.getStringExtra("name")!!
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
        val tvPlayer: TextView = findViewById(R.id.tv_player)
        tvPlayer.text = player.name
        tvScore = findViewById(R.id.tv_score)
        tvScore.text = "Score: ${player.score}"

        // Remove player
        val btEnd: Button = findViewById(R.id.bt_end)
        btEnd.setOnClickListener {
            database.deletePlayerInLobby(db, playerKey, lobby)
            finish()
        }

        val gameFunctionalities = GameFunctionalities(this)
        gameFunctionalities.runGame(player.difficulty)

        val btAnswer: Button = findViewById(R.id.bt_answer)
        btAnswer.setOnClickListener {
            getResponse(gameFunctionalities)
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

    fun displayValues(x: Int, y: Int, op: String) {
        val num1: TextView = findViewById(R.id.tv_num1)
        val num2: TextView = findViewById(R.id.tv_num2)
        val operator: TextView = findViewById(R.id.tv_signal)

        num1.text = "$x"
        num2.text = "$y"
        operator.text = op
    }

    @SuppressLint("SetTextI18n")
    fun getResponse(gameFunctionalities: GameFunctionalities) {
        val answer: EditText = findViewById(R.id.et_answer)
        val isCorrect: TextView = findViewById(R.id.tv_isCorrect)
         if (gameFunctionalities.checkAnswer(answer.text.toString(), correctAnswer)) {
             isCorrect.text = "Correct Answer"
             gameFunctionalities.correctAnswer(player)
         } else {
             isCorrect.text = "Wrong Answer"
             gameFunctionalities.incorrectAnswer(player)
         }
    }

    @SuppressLint("SetTextI18n")
    fun updateScore(score: String) {
        tvScore = findViewById(R.id.tv_score)
        tvScore.text = "Score: $score"
    }
}