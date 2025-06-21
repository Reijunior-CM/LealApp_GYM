package com.example.lealapp

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.lealapp.adapter.ImageAdapter
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.google.android.gms.tasks.Tasks

class TreinoA : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_training_a)
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
                val limitedRefs = list.items.take(6)
                val tasks = limitedRefs.map { it.downloadUrl }
                Tasks.whenAllSuccess<android.net.Uri>(tasks).addOnSuccessListener { uris ->
                    uris.forEachIndexed { idx, uri ->
                        val name = limitedRefs[idx].name.substringBeforeLast('.')
                        val series = if (name.lowercase() in listOf("escada","corrida")) "30-120min" else "3x 12 a 15"
                        val desc = when(name.lowercase()) {
                            "agachamento com haltere"      -> "Trabalha pernas (quadríceps), glúteos e core"
                            "cadeira extensora"            -> "Isola e fortalece o quadríceps"
                            "corrida esteira"              -> "Melhora o condicionamento cardiovascular"
                            "crucifixo polia"              -> "Trabalha o peitoral com foco na abertura dos braços"
                            "desenvolvimento aparelho"     -> "Fortalece ombros (deltoides) e tríceps"
                            "elevacao panturrilha maquina"-> "Trabalha a musculatura das panturrilhas"
                            "elevacao pernas paralela"     -> "Foca no abdômen inferior"
                            "escada"                        -> "Cardio com ênfase em pernas e glúteos"
                            "flexao braco"                 -> "Trabalha peitoral, tríceps e ombros"
                            "leg press_45"                 -> "Trabalha quadríceps, posteriores e glúteos"
                            "puxada alta frente"           -> "Fortalece costas (dorsais) e bíceps"
                            "rosca alternada"              -> "Exercício de bíceps com foco unilateral"
                            "rosca direta barra"           -> "Trabalha o bíceps de forma direta e bilateral"
                            "rosca punho haltere"          -> "Fortalece antebraços"
                            "stiff"                        -> "Foca nos posteriores de coxa e glúteos"
                            "supino reto barra"            -> "Trabalha peitoral, tríceps e ombros"
                            "triceps polia"                -> "Isola e fortalece o tríceps"
                            "triceps testa polia"          -> "Foca na cabeça longa do tríceps"
                            else -> "Exercício de musculação"
                        }
                        com.example.lealapp.ExerciseRegistry.usedNames.add(name)
                        items.add(com.example.lealapp.adapter.ImageItem(uri.toString(), name, desc, series))
                    }
                    rv.adapter = com.example.lealapp.adapter.ImageAdapter(items) { /* click */ }
                    Toast.makeText(this, "${items.size} imagens carregadas", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener { e ->
                Log.e("TreinoA", "Erro ao acessar Storage: ${e.message}")
                Toast.makeText(this, "Falha ao acessar Firebase Storage", Toast.LENGTH_LONG).show()
            }
        }

        btn.setOnClickListener { ensureAuthThenLoad(loadImages) }

        // Carrega automaticamente na abertura
        ensureAuthThenLoad(loadImages)
    }

    private fun ensureAuthThenLoad(action: () -> Unit) {
        val auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {
            action()
        } else {
            auth.signInAnonymously().addOnSuccessListener {
                action()
            }.addOnFailureListener { e ->
                Log.e("TreinoA", "Erro auth anônima: ${e.message}")
                Toast.makeText(this, "Falha na autenticação Firebase", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun btnBacka(view: android.view.View) {
        finish()
    }
}