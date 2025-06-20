package com.example.lealapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RegistrationActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registration)

        auth = FirebaseAuth.getInstance()
        db = Firebase.firestore

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

    }

    fun register(view: android.view.View) {
        val emailEditText = findViewById<EditText>(R.id.editTextEmail)
        val passwordEditText = findViewById<EditText>(R.id.editTextPassword)
        val confirmPasswordEditText = findViewById<EditText>(R.id.editTextConfirmPassword)

        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()
        val confirmPassword = confirmPasswordEditText.text.toString()

        // Check if passwords match
        if (email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()) {
            if (password != confirmPassword) {
                Toast.makeText(this, getString(R.string.passwords_do_not_match), Toast.LENGTH_SHORT).show()
                return
            }
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        user?.let { currentUser ->
                            val userData = hashMapOf(
                                "email" to email,
                                "name" to "",
                                "registrationDate" to System.currentTimeMillis()
                            )

                            db.collection("users").document(currentUser.uid)
                                .set(userData)
                                .addOnSuccessListener {
                                    Toast.makeText(
                                        this,
                                        getString(R.string.registration_successful),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    goBackToLogin()
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(
                                        this,
                                        getString(R.string.error_saving_data) + ": ${e.message}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    goBackToLogin()
                                }
                        }
                    } else {
                        Toast.makeText(
                            this,
                            task.exception?.message ?: getString(R.string.registration_error),
                            Toast.LENGTH_LONG
                        ).show()
                        goBackToLogin()
                    }
                }
        } else {
            Toast.makeText(this, getString(R.string.fill_in_all_fields), Toast.LENGTH_SHORT).show()
        }
    }

    private fun goBackToLogin() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
