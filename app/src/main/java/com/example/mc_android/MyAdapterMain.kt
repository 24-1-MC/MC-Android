package com.example.mc_android

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mc_android.databinding.ListItemMainBinding

class MyAdapterMain(private val itemList: List<String>) : RecyclerView.Adapter<MyAdapterMain.ViewHolder>() {

    class ViewHolder(private val binding: ListItemMainBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: String) {
            binding.itemText.text = item
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListItemMainBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(itemList[position])
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}
