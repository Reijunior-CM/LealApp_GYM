package com.example.lealapp

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainScreenActivity : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()
    private val db = Firebase.firestore

    private lateinit var editName: EditText
    private lateinit var editAge: EditText
    private lateinit var editHeight: EditText
    private lateinit var editWeight: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main_screen)


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        editName = findViewById(R.id.editName)
        editAge = findViewById(R.id.editAge)
        editHeight = findViewById(R.id.editHeight)
        editWeight  = findViewById(R.id.editWeight)

        carregarPerfil()
    }

    private fun loadProfile() {
        auth.currentUser?.let { user ->
            db.collection("users").document(user.uid).get()
                .addOnSuccessListener { doc ->
                    if (doc != null && doc.exists()) {
                        editName.setText(doc.getString("name") ?: "")
                        doc.getLong("age")?.let { editAge.setText(it.toString()) }
                        doc.getDouble("height")?.let { editHeight.setText(it.toString()) }
                        doc.getDouble("weight")?.let { editWeight.setText(it.toString()) }
                    }
                }
        }
    }

    // Função chamada pelo botão
    fun saveProfileButton(view: android.view.View) {
        val name = editName.text.toString()
        val age = editAge.text.toString().toIntOrNull()
        val height = editHeight.text.toString().toDoubleOrNull()
        val weight = editWeight.text.toString().toDoubleOrNull()

        if (name.isNotEmpty() && age != null && height != null && weight != null) {
            val intent = Intent(this, ExercisesActivity::class.java)
            intent.putExtra("name", name)
            intent.putExtra("age", age)
            intent.putExtra("height", height)
            intent.putExtra("weight", weight)
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(this, getString(R.string.fill_in_all_fields_correctly), Toast.LENGTH_SHORT).show()
        }
    }


    private fun saveProfile(
        name: String,
        age: Int,
        height: Double,
        weight: Double,
        onDone: () -> Unit
    ) {
        auth.currentUser?.let { user ->
            val data = hashMapOf(
                "name" to name,
                "age" to age,
                "height" to height,
                "weight" to weight
            )
            db.collection("users").document(user.uid)
                .set(data, SetOptions.merge())
                .addOnSuccessListener { onDone() }
                .addOnFailureListener {
                    Toast.makeText(this, getString(R.string.error_saving_data), Toast.LENGTH_SHORT).show()
                    onDone() // even on error, proceed to next screen
                }
        } ?: run {
            // User not logged in, just proceed
            onDone()
        }
    }
}
