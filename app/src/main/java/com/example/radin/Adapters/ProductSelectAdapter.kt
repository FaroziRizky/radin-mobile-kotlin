package com.example.radin.Adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.radin.MainPageActivity.ProductPageActivity.FruitsFragment
import com.example.radin.MainPageActivity.ProductPageActivity.GroceriesFragment
import com.example.radin.MainPageActivity.ProductPageActivity.MeatsFragment

class ProductSelectAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> GroceriesFragment()  // Assume you have ColdItem fragment for "Dingin"
            1 -> MeatsFragment()  // Assume you have ColdItem fragment for "Dingin"
            else -> FruitsFragment()
        }
    }

    override fun getCount(): Int {
        return 3
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return null  // Titles are set in the custom tab view
    }
}
