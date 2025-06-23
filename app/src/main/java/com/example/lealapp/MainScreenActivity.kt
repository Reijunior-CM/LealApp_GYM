package com.example.lealapp

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.lealapp.MainScreenViewModel
import com.example.lealapp.model.UserProfile

class MainScreenActivity : AppCompatActivity() {

    private val viewModel: MainScreenViewModel by viewModels()

    private lateinit var editName: EditText
    private lateinit var editAge: EditText
    private lateinit var editHeight: EditText
    private lateinit var editWeight: EditText

    // Método chamado ao criar a tela. Inicializa componentes e observa dados do ViewModel.
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
        editWeight = findViewById(R.id.editWeight)

        observeViewModel()
        viewModel.loadProfile()
    }

    // Observa as mudanças do perfil e do status de salvamento no ViewModel.
    private fun observeViewModel() {
        viewModel.profile.observe(this) { profile ->
            profile?.let {
                editName.setText(it.name)
                editAge.setText(it.age.toString())
                editHeight.setText(it.height.toString())
                editWeight.setText(it.weight.toString())
            }
        }
        viewModel.saveStatus.observe(this) { saved ->
            if (saved) {
                navigateToExercises()
            } else {
                Toast.makeText(this, getString(R.string.error_saving_data), Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Salva o perfil do usuário ao clicar no botão.
    fun saveProfileButton(view: android.view.View) {
        val name = editName.text.toString()
        val age = editAge.text.toString().toIntOrNull()
        val height = editHeight.text.toString().toDoubleOrNull()
        val weight = editWeight.text.toString().toDoubleOrNull()

        if (name.isNotEmpty() && age != null && height != null && weight != null) {
            val profile = UserProfile(name, age, height, weight)
            viewModel.saveProfile(profile)
        } else {
            Toast.makeText(this, getString(R.string.fill_in_all_fields_correctly), Toast.LENGTH_SHORT).show()
        }
    }

    // Navega para a tela de exercícios, passando os dados do perfil.
    private fun navigateToExercises() {
        val intent = Intent(this, Exercicios::class.java).apply {
            val ageSafe = editAge.text.toString().toIntOrNull() ?: 0
            val heightSafe = editHeight.text.toString().toDoubleOrNull() ?: 0.0
            val weightSafe = editWeight.text.toString().toDoubleOrNull() ?: 0.0
            putExtra("name", editName.text.toString())
            putExtra("age", ageSafe)
            putExtra("height", heightSafe)
            putExtra("weight", weightSafe)
        }
        startActivity(intent)
        finish()
    }
}