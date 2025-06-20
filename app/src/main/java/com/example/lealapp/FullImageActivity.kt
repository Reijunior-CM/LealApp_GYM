package com.example.lealapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class FullImageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_full_image)

        // Get image URL from intent and display it
        val imageUrl = intent.getStringExtra("url")
        val imageView = findViewById<android.widget.ImageView>(R.id.ivFull)
        Glide.with(this).load(imageUrl).into(imageView)

        imageView.setOnClickListener { finish() }
    }
}
