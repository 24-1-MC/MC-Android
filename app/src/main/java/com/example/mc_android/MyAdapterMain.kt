package com.example.mc_android

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mc_android.databinding.ListItemMainBinding

class MyAdapterMain(val itemList: MutableList<String>, private val itemClickListener: OnItemClickListener) :
    RecyclerView.Adapter<MyAdapterMain.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(position: Int)
        fun onItemLongClick(position: Int)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener, View.OnLongClickListener {
        val itemText: TextView = itemView.findViewById(R.id.item_text)

        init {
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                itemClickListener.onItemClick(position)
            }
        }

        override fun onLongClick(v: View?): Boolean {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                itemClickListener.onItemLongClick(position)
            }
            return true
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_main, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemText.text = itemList[position]
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}
