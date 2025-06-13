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

    // Firebase Auth. de Autenticação
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

        // "Cadastra-se"
        val textView = findViewById<TextView>(R.id.textCadastro)
        textView.setOnClickListener {
            val intent = Intent(this, Cadastro::class.java)
            startActivity(intent)
        }
    }

    //Verifica se o usuario tem login
    fun logar(view: View) {
        val email = findViewById<EditText>(R.id.textEmail).text.toString()
        val senha = findViewById<EditText>(R.id.textSenha).text.toString()

        if (email.isNotEmpty() && senha.isNotEmpty()) {
            auth.signInWithEmailAndPassword(email, senha)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        user?.let { current ->
                            db.collection("usuarios").document(current.uid).get()
                                .addOnSuccessListener { doc ->
                                    if (doc != null && doc.exists() &&
                                        doc.contains("nome") && doc.contains("altura") && doc.contains("peso") && doc.contains("idade")) {
                                        val nome = doc.getString("nome") ?: "Usuário"
                                        val idade = doc.getLong("idade")?.toInt() ?: 0
                                        val altura = doc.getDouble("altura") ?: 0.0
                                        val peso = doc.getDouble("peso") ?: 0.0

                                        val intentEx = Intent(this, Exercicios::class.java)
                                        intentEx.putExtra("nome", nome)
                                        intentEx.putExtra("idade", idade)
                                        intentEx.putExtra("altura", altura)
                                        intentEx.putExtra("peso", peso)
                                        startActivity(intentEx)
                                        finish()
                                    } else {
                                        // Perfil não completo, vai para Telaprincipal
                                        val intentTel = Intent(this, Telaprincipal::class.java)
                                        startActivity(intentTel)
                                        finish()
                                    }
                                }
                        }
                    } else {
                        Toast.makeText(this, "Usuário ou senha incorretos!", Toast.LENGTH_LONG).show()
                    }
                }
        } else {
            Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
        }

        }
    }
