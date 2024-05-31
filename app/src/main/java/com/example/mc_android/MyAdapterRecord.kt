package com.example.mc_android

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mc_android.databinding.ListItemRecordBinding

class MyAdapterRecord(private val itemList: List<String>) : RecyclerView.Adapter<MyAdapterRecord.ViewHolder>() {

    class ViewHolder(private val binding: ListItemRecordBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: String) {
            binding.itemText.text = item
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListItemRecordBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(itemList[position])
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}
