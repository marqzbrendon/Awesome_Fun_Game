package com.example.awesomefungame

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.*
import android.widget.TextView.OnEditorActionListener
import androidx.appcompat.app.AppCompatActivity
import com.example.mybudgetapp.LobbyPlayers
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlin.random.Random


class GameActivity : AppCompatActivity() {

    // Private variables
    private lateinit var lobby: String
    private val db = Firebase.firestore
    private lateinit var database: AppDatabase
    private val player = Player()
    private lateinit var tvScore: TextView
    private lateinit var tvLives: TextView
    private lateinit var btAnswer: Button

    // Public variable
    lateinit var correctAnswer: String

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        // Initiate player instance
        val playerName = intent.getStringExtra("name")!!
        player.name = playerName

        val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        val playerKey = (1..15)
            .map { kotlin.random.Random.nextInt(0, charPool.size) }
            .map(charPool::get)
            .joinToString("")
        player.playerKey = playerKey

        lobby = intent.getStringExtra("lobby").toString()
        if (lobby == "None") {
            val random = (1..4).random()
            lobby = random.toString()
        }
        player.lobby = lobby

        // Lobby data
        val lobbyPlayers = LobbyPlayers()

        // Initiate database instance
        database = AppDatabase(this)

        // Add new player to Lobby database
        database.addPlayerInLobby(db, player)

        // Getting all the lobby data from the database
        val adapter: ArrayAdapter<Any> = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            arrayListOf("Retrieving data...") as List<Any>
        )
        adapter.notifyDataSetChanged()
        val lvPlayers: ListView = findViewById(R.id.lv_players)
        lvPlayers.adapter = adapter
        database.retrieveAllDocuments(db, player.lobby, lobbyPlayers, adapter, lvPlayers)

        // Display top information
        val tvPlayer: TextView = findViewById(R.id.tv_player)
        tvPlayer.text = player.name
        tvScore = findViewById(R.id.tv_score)
        tvScore.text = "Score: ${player.score}"
        tvLives =  findViewById(R.id.tv_lives)
        tvLives.text = "Lives: ${player.lives}"

        // Remove player
        val btEnd: Button = findViewById(R.id.bt_end)
        btEnd.setOnClickListener {
            database.deletePlayerInLobby(db, player)
            finish()
        }

        val gameFunctionalities = GameFunctionalities(this)
        gameFunctionalities.runGame(player.difficulty)

        btAnswer = findViewById(R.id.bt_answer)
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
        database.deletePlayerInLobby(db, player)
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
        answer.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_DONE) {
                //do what you want on the press of 'done'
                btAnswer.performClick()
            }
            false
        })
         if (gameFunctionalities.checkAnswer(answer.text.toString(), correctAnswer)) {
             isCorrect.text = "Correct Answer"
             gameFunctionalities.correctAnswer(player)
         } else {
             isCorrect.text = "Wrong Answer"
             gameFunctionalities.incorrectAnswer(player)
         }
    }

    @SuppressLint("SetTextI18n")
    fun updateScore() {
        tvScore = findViewById(R.id.tv_score)
        tvScore.text = "Score: ${player.score}"
        tvLives = findViewById(R.id.tv_lives)
        tvLives.text = "Lives: ${player.lives}"
        database.updateScore(player, db)
    }

    fun gameOver() {
        val intent = Intent(this, GameOverActivity::class.java)
        intent.putExtra("playerName", player.name)
        intent.putExtra("playerScore", player.score.toString())
        intent.putExtra("lobby", player.lobby)
        intent.putExtra("playerKey", player.playerKey)
        startActivity(intent)
    }
}