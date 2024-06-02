package com.example.mc_android

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mc_android.databinding.FragmentRecordBinding

class RecordFragment : Fragment() {
    // nullable binding 꼼수
    private var _binding: FragmentRecordBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRecordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val itemList = listOf("Record 1", "Record 2", "Record 3", "Record 4", "Record 5") // 샘플 데이터
        binding.recyclerViewRecord.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewRecord.adapter = MyAdapterRecord(itemList)
        binding.debugMap.setOnClickListener {
            val intent: Intent = Intent(requireContext(), MapView::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
