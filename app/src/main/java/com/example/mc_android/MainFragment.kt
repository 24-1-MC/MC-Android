package com.example.mc_android

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mc_android.databinding.FragmentMainBinding
import com.example.mc_android.mydata.MyDataDaoDatabase
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainFragment : Fragment(), MyAdapterMain.OnItemClickListener {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private val markedDates = listOf(
        CalendarDay.from(2024, 6, 1),
        CalendarDay.from(2024, 6, 2),
        CalendarDay.from(2024, 6, 3)
    ) // 표시할 날짜 목록 (예시)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    private var adapter: MyAdapterMain? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        val db = MyDataDaoDatabase.getDatabase(requireContext())

        db?.myDataDao()?.selectAll()?.observe(viewLifecycleOwner, Observer { myDataList ->
            adapter = MyAdapterMain(myDataList.toMutableList(), this)
            binding.recyclerView.adapter = adapter
        })

        setupCalendarView()
    }

    private fun setupCalendarView() {
        val calendarView = binding.calendarView

        // 날짜 선택 비활성화 설정
        calendarView.selectionMode = MaterialCalendarView.SELECTION_MODE_NONE

        // 날짜 클릭 리스너 제거
        calendarView.setOnDateChangedListener(null)

        // 터치 이벤트를 완전히 차단하는 빈 리스너 설정
        calendarView.setOnTouchListener { _, _ -> true }

        // 특정 날짜 표시 데코레이터 추가
        calendarView.addDecorators(object : DayViewDecorator {
            override fun shouldDecorate(day: CalendarDay?): Boolean {
                return markedDates.contains(day)
            }

            override fun decorate(view: DayViewFacade?) {
                view?.setBackgroundDrawable(resources.getDrawable(R.drawable.custom_date_selector, null))
            }
        })
    }

    override fun onItemClick(position: Int) {
        val selectedItem = adapter?.itemList?.get(position)

        selectedItem?.let {
            val intent = Intent(requireContext(), RecordActivity::class.java)
            intent.putExtra("selectedItem", it)
            startActivity(intent)
        }
    }

    override fun onItemLongClick(position: Int) {
        val db = MyDataDaoDatabase.getDatabase(requireContext())
        val selectedItem = adapter?.itemList?.get(position)
        // AlertDialog를 사용하여 삭제 여부 확인
        AlertDialog.Builder(requireContext())
            .setTitle("삭제 확인")
            .setMessage("이 기록을을 삭제하시겠습니까?")
            .setPositiveButton("예") { dialog, which ->
                CoroutineScope(Dispatchers.IO).launch {
                    db!!.myDataDao().deleteById(selectedItem!!.id)
                    withContext(Dispatchers.Main) {
                        adapter?.itemList?.removeAt(position)
                        adapter?.notifyItemRemoved(position)
                    }
                }
            }
            .setNegativeButton("아니오", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
