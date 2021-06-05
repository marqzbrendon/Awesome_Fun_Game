package com.example.awesomefungame

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        val btStart: Button = findViewById(R.id.bt_start)
        btStart.setOnClickListener { startApp() }
    }

    private fun startApp() {
        val et_name: EditText = findViewById(R.id.et_name)
        val intent = Intent(this, GameActivity::class.java)
        intent.putExtra("name", et_name.text.toString())
        startActivity(intent)
    }
}