package com.example.radin.Adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.radin.MainPageActivity.HistoryPageActivity.HistoryAllFragment
import com.example.radin.MainPageActivity.HistoryPageActivity.ProcessFragment
import com.example.radin.MainPageActivity.HistoryPageActivity.UnpaidFragment

class HistorySelectAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> UnpaidFragment()
            1 -> ProcessFragment()
            else -> HistoryAllFragment()
        }
    }

    override fun getCount(): Int {
        return 3
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return null  // Titles are set in the custom tab view
    }
}