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


class LoginMobile : AppCompatActivity() {

    /**
     * This is a easy login with email (Firebase)
     */

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_mobile)

        auth = Firebase.auth

        val textView3: TextView = findViewById(R.id.textView3)
        textView3.setOnClickListener {
            val intent = Intent(this, RegisterEmail::class.java)
            startActivity(intent)
        }

        val loginButton: Button = findViewById(R.id.LoginNowButton)
        loginButton.setOnClickListener {
            preformLogin()
        }
    }

    private fun preformLogin() {
        // Getting user input
        val email: EditText = findViewById(R.id.EnterEmail)
        val password: EditText = findViewById(R.id.EnterPassword)
        // Null checks on input
        if (email.text.isEmpty() || password.text.isEmpty()){
            Toast.makeText(this, "Please fill out all the fields", Toast.LENGTH_SHORT)
                .show()
            return
        }

        val emailInput = email.text.toString()
        val passwordInput = password.text.toString()

        auth.signInWithEmailAndPassword(emailInput, passwordInput)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed. ${task.exception}",
                        Toast.LENGTH_SHORT).show()

                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error ${it.localizedMessage}", Toast.LENGTH_SHORT)
                    .show()
            }

    }
}