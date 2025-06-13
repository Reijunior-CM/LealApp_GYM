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

class Cadastro : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cadastro)

        auth = FirebaseAuth.getInstance()
        db = Firebase.firestore

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

    }

    fun cadastrar(view: android.view.View) {
        val emailEditText = findViewById<EditText>(R.id.textEmail)
        val senhaEditText = findViewById<EditText>(R.id.textSenha)
        val senhaConfirmEditText = findViewById<EditText>(R.id.textSenhaC)

        val email = emailEditText.text.toString()
        val senha = senhaEditText.text.toString()
        val senhaC = senhaConfirmEditText.text.toString()

        // Confirma se as senhas batem
        if (email.isNotEmpty() && senha.isNotEmpty() && senhaC.isNotEmpty()) {
            if (senha != senhaC) {
                Toast.makeText(this, "As senhas não coincidem!", Toast.LENGTH_SHORT).show()
                return
            }
            auth.createUserWithEmailAndPassword(email, senha)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        user?.let { currentUser ->
                            val userData = hashMapOf(
                                "email" to email,
                                "nome" to "",
                                "dataCadastro" to System.currentTimeMillis()
                            )

                            db.collection("usuarios").document(currentUser.uid)
                                .set(userData)
                                .addOnSuccessListener {
                                    Toast.makeText(
                                        this,
                                        "Cadastro realizado com sucesso!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    voltarLogin()
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(
                                        this,
                                        "Erro ao salvar dados: ${e.message}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    voltarLogin()
                                }
                        }
                    } else {
                        Toast.makeText(
                            this,
                            task.exception?.message ?: "Erro ao cadastrar!",
                            Toast.LENGTH_LONG
                        ).show()
                        voltarLogin()
                    }
                }
        } else {
            Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun voltarLogin() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
