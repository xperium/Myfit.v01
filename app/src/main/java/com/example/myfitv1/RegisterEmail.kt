package com.example.myfitv1

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RegisterEmail : AppCompatActivity() {

    /**
     * Register email.
     */

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_email)

        // Initialize Firebase Auth
        auth = Firebase.auth

        val goToLogin2: TextView = findViewById(R.id.GoToLogin2)
        goToLogin2.setOnClickListener {
            val intent = Intent(this, StartScreen::class.java)
            startActivity(intent)
        }

        val registerButton: Button = findViewById(R.id.RegNowButton)
        registerButton.setOnClickListener{
            preformSignUp()
        }


// Getting email and password from user


    }

    private fun preformSignUp() {
        val email = findViewById<EditText>(R.id.retypeEmail)
        val password = findViewById<EditText>(R.id.retypePassword)

        if (email.text.isEmpty() || password.text.isEmpty()){
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT)
                .show()
            return
        }



        val inputEmail = email.text.toString()
        val inputPassword = password.text.toString()


        auth.createUserWithEmailAndPassword(inputEmail, inputPassword)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed. $",
                        Toast.LENGTH_SHORT).show()

                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error ${it.localizedMessage}", Toast.LENGTH_SHORT)
                    .show()
            }
    }
}