package com.example.myfitv1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button




class WorkoutMain : AppCompatActivity() {
    // Declaring a Button to sign out the user
    private lateinit var createWorkout: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_main)


        // Getting a reference to the sign out button
        createWorkout = findViewById(R.id.createWorkout)


        createWorkout.setOnClickListener {

            // Go to the Workout screen
            val intent = Intent(this, CreateWorkoutPage1::class.java)
            startActivity(intent)
            finish()
        }
    }
}