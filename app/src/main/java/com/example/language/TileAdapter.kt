package com.example.language

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TileAdapter(
    private val items: List<TileItem>,
    private val onClick: (TileItem) -> Unit // Handles clicks
) : RecyclerView.Adapter<TileAdapter.TileViewHolder>() {

    // Creates the visual tile from your XML
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TileViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_tile, parent, false)
        return TileViewHolder(view)
    }

    // Fills the tile with data (Text, Colors)
    override fun onBindViewHolder(holder: TileViewHolder, position: Int) {
        val item = items[position]

        holder.tvLanguage.text = item.language
        holder.tvMainText.text = item.title

        // Click listener for the specific tile
        holder.itemView.setOnClickListener { onClick(item) }
    }

    override fun getItemCount() = items.size

    // Connects to the IDs in item_tile.xml
    class TileViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvLanguage: TextView = view.findViewById(R.id.tv_tile_language)
        val tvMainText: TextView = view.findViewById(R.id.tv_tile_main_text)
    }
}