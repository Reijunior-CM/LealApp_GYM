package com.example.lealapp

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lealapp.adapter.ImageAdapter
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.google.android.gms.tasks.Tasks
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Aleatorio : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_aleatorio)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val rv = findViewById<RecyclerView>(R.id.rvImages)
        rv.layoutManager = LinearLayoutManager(this)
        val btn = findViewById<MaterialButton>(R.id.btnAddTreino)

        val loadImages: () -> Unit = {
            Toast.makeText(this, "Carregando imagens...", Toast.LENGTH_SHORT).show()
            Firebase.storage.reference.child("img").listAll().addOnSuccessListener { list ->
                val items = mutableListOf<com.example.lealapp.adapter.ImageItem>()
                val limitedRefs = list.items.filter { !com.example.lealapp.ExerciseRegistry.usedNames.contains(it.name.substringBeforeLast('.')) }.take(6)
                val tasks = limitedRefs.map { it.downloadUrl }
                Tasks.whenAllSuccess<android.net.Uri>(tasks).addOnSuccessListener { uris ->
                    uris.forEachIndexed { idx, uri ->
                        val name = limitedRefs[idx].name.substringBeforeLast('.')
                        val series = if (name.lowercase() in listOf("escada","corrida")) "30-120min" else "3x 12 a 15"
                        val desc = "Exercício diferenciado"
                        com.example.lealapp.ExerciseRegistry.usedNames.add(name)
                        items.add(com.example.lealapp.adapter.ImageItem(uri.toString(), name, desc, series))
                    }
                    rv.adapter = ImageAdapter(items.toMutableList()) { }
                    Toast.makeText(this, "${items.size} imagens carregadas", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener { e ->
                Log.e("Aleatorio", "Erro ao acessar Storage: ${e.message}")
                Toast.makeText(this, "Falha ao acessar Firebase Storage", Toast.LENGTH_LONG).show()
            }
        }

        btn.setOnClickListener { ensureAuthThenLoad(loadImages) }
        ensureAuthThenLoad(loadImages)
    }

    private fun ensureAuthThenLoad(action: () -> Unit) {
        val auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {
            action()
        } else {
            auth.signInAnonymously().addOnSuccessListener { action() }
                .addOnFailureListener { e ->
                    Log.e("Aleatorio", "Auth erro: ${e.message}")
                    Toast.makeText(this, "Falha na autenticação Firebase", Toast.LENGTH_LONG).show()
                }
        }
    }

    fun btnBackal(view: android.view.View) {
        val intent = Intent(this, Exercicios::class.java)
        startActivity(intent)
        finish()
    }
}
