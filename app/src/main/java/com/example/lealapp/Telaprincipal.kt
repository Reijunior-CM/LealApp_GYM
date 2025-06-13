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

class Telaprincipal : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()
    private val db = Firebase.firestore

    private lateinit var editNome: EditText
    private lateinit var editIdade: EditText
    private lateinit var editAltura: EditText
    private lateinit var editPeso: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_telaprincipal)


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        editNome = findViewById(R.id.editNome)
        editIdade = findViewById(R.id.editIdade)
        editAltura = findViewById(R.id.editAltura)
        editPeso  = findViewById(R.id.editPeso)

        carregarPerfil()
    }

    private fun carregarPerfil() {
        auth.currentUser?.let { user ->
            db.collection("usuarios").document(user.uid).get()
                .addOnSuccessListener { doc ->
                    if (doc != null && doc.exists()) {
                        editNome.setText(doc.getString("nome") ?: "")
                        doc.getLong("idade")?.let { editIdade.setText(it.toString()) }
                        doc.getDouble("altura")?.let { editAltura.setText(it.toString()) }
                        doc.getDouble("peso")?.let { editPeso.setText(it.toString()) }
                    }
                }
        }
    }

    // Função chamada pelo botão
    fun btSalvar(view: android.view.View) {
        val nome = editNome.text.toString()
        val idade = editIdade.text.toString().toIntOrNull()
        val altura = editAltura.text.toString().toDoubleOrNull()
        val peso = editPeso.text.toString().toDoubleOrNull()

        if (nome.isNotEmpty() && idade != null && altura != null && peso != null) {
            val intent = Intent(this, Exercicios::class.java)
            intent.putExtra("nome", nome)
            intent.putExtra("idade", idade)
            intent.putExtra("altura", altura)
            intent.putExtra("peso", peso)
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(this, "Preencha todos os campos corretamente!", Toast.LENGTH_SHORT).show()
        }
    }


    private fun salvarPerfil(
        nome: String,
        idade: Int,
        altura: Double,
        peso: Double,
        onDone: () -> Unit
    ) {
        auth.currentUser?.let { user ->
            val dados = hashMapOf(
                "nome" to nome,
                "idade" to idade,
                "altura" to altura,
                "peso" to peso
            )
            db.collection("usuarios").document(user.uid)
                .set(dados, SetOptions.merge())
                .addOnSuccessListener { onDone() }
                .addOnFailureListener {
                    Toast.makeText(this, "Erro ao salvar dados", Toast.LENGTH_SHORT).show()
                    onDone() // mesmo com erro, segue para próxima tela
                }
        } ?: run {
            // Usuário não logado, apenas navega
            onDone()
        }
    }
}
