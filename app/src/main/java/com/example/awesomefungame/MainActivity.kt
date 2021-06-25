package com.example.awesomefungame

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val lobby1: Button = findViewById(R.id.selection1)
        val lobby2: Button = findViewById(R.id.selection2)
        val lobby3: Button = findViewById(R.id.selection3)
        val lobby4: Button = findViewById(R.id.selection4)

        var selectedLobby = "None"
        setLobbyDisplay(selectedLobby)


        lobby1.setOnClickListener {
            selectedLobby = "1"
            setLobbyDisplay(selectedLobby)
        }
        lobby2.setOnClickListener {
            selectedLobby = "2"
            setLobbyDisplay(selectedLobby)
        }
        lobby3.setOnClickListener {
            selectedLobby = "3"
            setLobbyDisplay(selectedLobby)
        }
        lobby4.setOnClickListener {
            selectedLobby = "4"
            setLobbyDisplay(selectedLobby)
        }

        val btStart: Button = findViewById(R.id.bt_start)
        btStart.setOnClickListener { startApp(selectedLobby) }
    }

    private fun startApp(selectedLobby: String) {
        val etName: EditText = findViewById(R.id.et_name)
        val intent = Intent(this, GameActivity::class.java)
        intent.putExtra("name", etName.text.toString())
        intent.putExtra("lobby", selectedLobby)
        startActivity(intent)
    }

    @SuppressLint("SetTextI18n")
    private fun setLobbyDisplay(selectedLobby: String) {
        val selectedDisplay: TextView = findViewById(R.id.tv_selected)
        selectedDisplay.text = "Lobby selected: $selectedLobby"
    }


}