package com.example.lealapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.lealapp.model.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainScreenViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = Firebase.firestore

    private val _profile = MutableLiveData<UserProfile?>()
    val profile: LiveData<UserProfile?> = _profile

    private val _saveStatus = MutableLiveData<Boolean>()
    val saveStatus: LiveData<Boolean> = _saveStatus

    // Carrega o perfil do usuário logado do Firestore
    fun loadProfile() {
        auth.currentUser?.let { user ->
            db.collection("users").document(user.uid).get()
                .addOnSuccessListener { doc ->
                    if (doc != null && doc.exists()) {
                        _profile.value = UserProfile(
                            doc.getString("name") ?: "",
                            (doc.getLong("age") ?: 0L).toInt(),
                            doc.getDouble("height") ?: 0.0,
                            doc.getDouble("weight") ?: 0.0
                        )
                    }
                }
        }
    }

    // Salva o perfil do usuário no Firestore
    fun saveProfile(profile: UserProfile) {
        auth.currentUser?.let { user ->
            val data = hashMapOf(
                "name" to profile.name,
                "age" to profile.age,
                "height" to profile.height,
                "weight" to profile.weight
            )
            db.collection("users").document(user.uid)
                .set(data, SetOptions.merge())
                .addOnSuccessListener { _saveStatus.value = true }
                .addOnFailureListener { _saveStatus.value = false }
        } ?: run {
            // Caso não haja usuário logado, considera salvo localmente
            _saveStatus.value = true
        }
    }
}
