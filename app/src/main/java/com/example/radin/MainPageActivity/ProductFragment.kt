package com.example.radin.MainPageActivity

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager.widget.ViewPager
import com.example.radin.Adapters.ProductSelectAdapter
import com.example.radin.MainPage
import com.example.radin.MainPageActivity.ProductPageActivity.CartPage
import com.example.radin.R
import com.google.android.material.tabs.TabLayout

class ProductFragment : Fragment() {

    private lateinit var viewPager: ViewPager
    private lateinit var tabs: TabLayout
    private lateinit var cartButton: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_product, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewPager = view.findViewById(R.id.viewpager_main)
        tabs = view.findViewById(R.id.tabs_main)
        cartButton = view.findViewById(R.id.cartButton)

        cartButton.setOnClickListener {
            activity?.let {
                val intent = Intent(it, CartPage::class.java)
                it.startActivity(intent)
            }
        }

        val fragmentAdapter = ProductSelectAdapter(childFragmentManager)
        viewPager.adapter = fragmentAdapter
        tabs.setupWithViewPager(viewPager)

        setupTabIcons()

        // Get the selected tab ID from arguments
        val selectedTabId = arguments?.getInt(MainPage.EXTRA_TAB_ID, -1) ?: -1
        if (selectedTabId >= 0) {
            viewPager.currentItem = selectedTabId
        }
    }

    private fun setupTabIcons() {
        val tabIcons = arrayOf(
            R.drawable.ic_groceries,
            R.drawable.ic_meats,
            R.drawable.ic_fruits
        )
        val tabTitles = arrayOf(
            "Sembako",
            "Daging",
            "Buah"
        )

        for (i in tabTitles.indices) {
            val tab = tabs.getTabAt(i)
            if (tab != null) {
                val customView = LayoutInflater.from(context).inflate(R.layout.custom_tab, null)
                customView.findViewById<TextView>(R.id.tabText).text = tabTitles[i]
                customView.findViewById<ImageView>(R.id.tabIcon).setImageResource(tabIcons[i])
                tab.customView = customView
            }
        }
    }
}
