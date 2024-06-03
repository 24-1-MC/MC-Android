package com.example.mc_android

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mc_android.databinding.FragmentMainBinding
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener


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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = MyAdapterMain(getSampleData(), this)

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
        val intent = Intent(requireContext(), RecordActivity::class.java)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getSampleData(): List<String> {
        return listOf("Item 1", "Item 2", "Item 3", "Item 4", "Item 5", "Item 6", "Item 7", "Item 8", "Item 9", "Item 10")
    }
}
