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

        val url = intent.getStringExtra("url")
        val iv = findViewById<android.widget.ImageView>(R.id.ivFull)
        Glide.with(this).load(url).into(iv)

        iv.setOnClickListener { finish() }
    }
}
