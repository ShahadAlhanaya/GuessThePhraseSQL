package com.example.guessthephrasesql

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_row.view.*


class MessagesRecyclerViewAdapter(private val messages: ArrayList<String>): RecyclerView.Adapter<MessagesRecyclerViewAdapter.MessagesViewHolder>() {
    class MessagesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessagesViewHolder {
        return MessagesViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_row,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MessagesViewHolder, position: Int) {
        val message = messages[position]
        holder.itemView.apply { messages_Textview.text = message}
    }

    override fun getItemCount() = messages.size
}