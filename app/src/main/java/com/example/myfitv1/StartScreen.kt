package com.example.myfitv1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth

class StartScreen : AppCompatActivity() {

    /**
     * The start screen for the user
     */



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_screen)

        val logInnMobile: Button = findViewById(R.id.logInnMobile)
        val logInnEmail: Button = findViewById(R.id.logInnEmail)

        logInnMobile.setOnClickListener {
            val intent = Intent(this, PhoneActivity::class.java)
            startActivity(intent)
        }


        logInnEmail.setOnClickListener {
            val intent = Intent(this, LoginMobile::class.java)
            startActivity(intent)
        }


        val regNow: TextView = findViewById(R.id.regNow)
        regNow.setOnClickListener {
            val intent = Intent(this, RegisterEmail::class.java)
            startActivity(intent)


        }

    }
}