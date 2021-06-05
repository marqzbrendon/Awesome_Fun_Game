package com.example.awesomefungame

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class GameActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        val tvMessage : TextView = findViewById(R.id.tv_message)
        tvMessage.text = "Get ready, ${intent.getStringExtra("name")}. An awesome game is about to start..."
    }
}