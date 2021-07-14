package com.example.awesomefungame

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.mybudgetapp.LobbyPlayers
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class GameActivity : AppCompatActivity() {

    // Private variables
    private lateinit var lobby: String
    private val db = Firebase.firestore
    private lateinit var database: AppDatabase
    private val player = Player()
    private lateinit var tvScore: TextView
    private lateinit var tvLives: TextView

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
        tvPlayer.text = "Player: ${player.name}"
        tvScore = findViewById(R.id.tv_score)
        tvScore.text = "Score: ${player.score}"
        tvLives = findViewById(R.id.tv_lives)
        tvLives.text = "Lives: ${player.lives}"

        // Remove player
        val btEnd: Button = findViewById(R.id.bt_end)
        btEnd.setOnClickListener {
            database.deletePlayerInLobby(db, player)
            finish()
        }

        val gameFunctionalities = GameFunctionalities(this)
        gameFunctionalities.runGame(player.difficulty)

        // Answer TextView
        val answer: EditText = findViewById(R.id.et_answer)
        answer.setOnEditorActionListener { _, actionId, event ->
            if (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_DONE) {
                //do what you want on the press of 'done'
                getResponse(answer, gameFunctionalities)
            }
            true
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

    @SuppressLint("SetTextI18n", "CutPasteId")
    fun getResponse(answer: EditText, gameFunctionalities: GameFunctionalities) {
        val view: View = findViewById(R.id.view)
        if (gameFunctionalities.checkAnswer(answer.text.toString(), correctAnswer)) {
            val snackBarCorrect = Snackbar
                .make(view, "Correct Answer", Snackbar.LENGTH_SHORT)
            val mView: View = snackBarCorrect.view
            val mTextView = mView.findViewById(R.id.snackbar_text) as TextView
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
                mTextView.textAlignment = View.TEXT_ALIGNMENT_CENTER;
            else
                mTextView.gravity = Gravity.CENTER_HORIZONTAL;
            snackBarCorrect.view.setBackgroundColor(Color.parseColor("#008000"))
            snackBarCorrect
                .show()
            gameFunctionalities.correctAnswer(player)
        } else {
            val snackBarIncorrect = Snackbar
                .make(view, "Wrong Answer", Snackbar.LENGTH_SHORT)
            val mView: View = snackBarIncorrect.view
            val mTextView = mView.findViewById(R.id.snackbar_text) as TextView
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
                mTextView.textAlignment = View.TEXT_ALIGNMENT_CENTER;
            else
                mTextView.gravity = Gravity.CENTER_HORIZONTAL;
            snackBarIncorrect.view.setBackgroundColor(Color.parseColor("#FF0000"))
            snackBarIncorrect
                .show()
            gameFunctionalities.incorrectAnswer(player)
        }
        answer.text.clear()
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