package com.example.lealapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import android.content.Intent
import com.example.lealapp.FullImageActivity
import com.example.lealapp.R

import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage


data class ImageItem(
    val url: String,
    val nome: String,
    val descricao: String,
    val series: String
)

class ImageAdapter(private val items: MutableList<ImageItem>, private val onClick: (String) -> Unit) :
    RecyclerView.Adapter<ImageAdapter.Holder>() {

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val img: ImageView = itemView.findViewById(R.id.img)
        val tvNome: android.widget.TextView = itemView.findViewById(R.id.tvName)
        val tvDesc: android.widget.TextView = itemView.findViewById(R.id.tvDesc)
        val tvSeries: android.widget.TextView = itemView.findViewById(R.id.tvSeries)
        val btnDelete: com.google.android.material.button.MaterialButton = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
        return Holder(v)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = items[position]
        Glide.with(holder.img).load(item.url).into(holder.img)
        holder.tvNome.text = item.nome
        holder.tvDesc.text = item.descricao
        holder.tvSeries.text = item.series
        
        holder.itemView.setOnClickListener {
            it.context.startActivity(Intent(it.context, FullImageActivity::class.java).putExtra("url", item.url))
            onClick(item.url)
        }
        holder.btnDelete.setOnClickListener {
            Firebase.storage.getReferenceFromUrl(item.url).delete()
            val pos = holder.adapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                items.removeAt(pos)
                notifyItemRemoved(pos)
            }
        }
    }
}
