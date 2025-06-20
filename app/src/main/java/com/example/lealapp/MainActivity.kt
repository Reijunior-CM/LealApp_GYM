package com.example.lealapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity() {

    // Firebase Authentication
    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = FirebaseAuth.getInstance()

        // "Sign Up"
        val signUpTextView = findViewById<TextView>(R.id.textSignUp)
        signUpTextView.setOnClickListener {
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
        }
    }

    // Checks if the user can log in
    fun login(view: View) {
        val email = findViewById<EditText>(R.id.editTextEmail).text.toString()
        val password = findViewById<EditText>(R.id.editTextPassword).text.toString()

        if (email.isNotEmpty() && password.isNotEmpty()) {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        user?.let { current ->
                            db.collection("users").document(current.uid).get()
                                .addOnSuccessListener { doc ->
                                    if (doc != null && doc.exists() &&
                                        doc.contains("name") && doc.contains("height") && doc.contains("weight") && doc.contains("age")) {
                                        val name = doc.getString("name") ?: getString(R.string.default_user)
                                        val age = doc.getLong("age")?.toInt() ?: 0
                                        val height = doc.getDouble("height") ?: 0.0
                                        val weight = doc.getDouble("weight") ?: 0.0

                                        val intentEx = Intent(this, ExercisesActivity::class.java)
                                        intentEx.putExtra("name", name)
                                        intentEx.putExtra("age", age)
                                        intentEx.putExtra("height", height)
                                        intentEx.putExtra("weight", weight)
                                        startActivity(intentEx)
                                        finish()
                                    } else {
                                        // Incomplete profile, go to MainScreenActivity
                                        val intentMain = Intent(this, MainScreenActivity::class.java)
                                        startActivity(intentMain)
                                        finish()
                                    }
                                }
                        }
                    } else {
                        Toast.makeText(this, getString(R.string.incorrect_username_or_password), Toast.LENGTH_LONG).show()
                    }
                }
        } else {
            Toast.makeText(this, getString(R.string.fill_in_all_fields), Toast.LENGTH_SHORT).show()
        }
    }
}
