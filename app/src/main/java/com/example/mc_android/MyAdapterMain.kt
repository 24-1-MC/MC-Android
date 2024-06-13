package com.example.mc_android

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mc_android.databinding.ListItemMainBinding
import com.example.mc_android.mydata.MyData
import com.example.mc_android.services.DateTimeUtils

class MyAdapterMain(val itemList: MutableList<MyData>, private val itemClickListener: OnItemClickListener) :
    RecyclerView.Adapter<MyAdapterMain.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(position: Int)
        fun onItemLongClick(position: Int)
    }

    inner class ViewHolder(private val binding: ListItemMainBinding) : RecyclerView.ViewHolder(binding.root), View.OnClickListener, View.OnLongClickListener {
        init {
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
        }

        fun bind(myData: MyData) {
            binding.itemDate.text = DateTimeUtils(myData.startAt).convertToLocalDate()
            binding.itemDistance.text = String.format("%.2fkm", myData.distance / 1000)
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
