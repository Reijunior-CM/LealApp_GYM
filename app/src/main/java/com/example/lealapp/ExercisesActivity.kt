package com.example.lealapp

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.Locale



class Exercicios : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_exercises)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }



        // Obtém dados enviados pela Telaprincipal
        val nome = intent.getStringExtra("nome") ?: "Usuário"
        val alturaCm = intent.getDoubleExtra("altura", 0.0) // altura em centímetros
        val peso = intent.getDoubleExtra("peso", 0.0)        // peso em kg

        // Referências das TextViews
        val tvNome = findViewById<TextView>(R.id.tvNomeUsuario)
        val tvImc = findViewById<TextView>(R.id.tvNivelIMC)

        tvNome.text = nome

        // Calcula o IMC se dados válidos
        if (alturaCm > 0 && peso > 0) {
            val alturaMetros = alturaCm / 100    // converte cm para metros
            val imc = peso / (alturaMetros * alturaMetros)
            val nivel = getNivelIMC(imc)
            tvImc.text = String.format(Locale.US, "IMC: %.2f (%s)", imc, nivel)
        } else {
            tvImc.text = "IMC: --"
        }
    }

    private fun getNivelIMC(imc: Double): String {
        return when {
            imc < 18.5 -> "Abaixo do peso"
            imc < 25 -> "Normal"
            imc < 30 -> "Sobrepeso"
            imc < 35 -> "Obesidade I"
            imc < 40 -> "Obesidade II"
            else -> "Obesidade III"
        }
    }


    fun TreinoA(view: android.view.View) {
        startActivity(android.content.Intent(this, TreinoA::class.java))

    }

    fun TreinoB(view: android.view.View) {
        startActivity(android.content.Intent(this, TreinoB::class.java))
    }

    fun TreinoC(view: android.view.View) {
        startActivity(android.content.Intent(this, TreinoC::class.java))
    }

    fun Aleatorio(view: android.view.View) {
        startActivity(android.content.Intent(this, Aleatorio::class.java))
    }
}
