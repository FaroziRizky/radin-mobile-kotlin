package com.example.radin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.radin.MainPageActivity.AccountFragment
import com.example.radin.MainPageActivity.HistoryFragment
import com.example.radin.MainPageActivity.HomeFragment
import com.example.radin.MainPageActivity.ProductFragment
import com.ismaeldivita.chipnavigation.ChipNavigationBar

class MainPage : AppCompatActivity() {

    companion object {
        const val EXTRA_FRAGMENT_ID = "extra_fragment_id"
        const val EXTRA_TAB_ID = "extra_tab_id"
        const val FRAGMENT_HOME = "home"
        const val FRAGMENT_MENU = "menu"
        const val FRAGMENT_HISTORY = "history"
        const val FRAGMENT_ACCOUNT = "account"
        const val TAB_SEMBAKO = 0
        const val TAB_DAGING = 1
        const val TAB_BUAH = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_page)
        supportActionBar?.hide()

        // Get the fragment ID and tab ID from the intent
        val fragmentId = intent.getStringExtra(EXTRA_FRAGMENT_ID)
        val tabId = intent.getIntExtra(EXTRA_TAB_ID, -1)

        openFragment(fragmentId ?: FRAGMENT_HOME, tabId)

        var menu_bottom = findViewById<ChipNavigationBar>(R.id.bottom_navigation)
        menu_bottom.setItemSelected(when(fragmentId) {
            FRAGMENT_MENU -> R.id.menu
            FRAGMENT_HISTORY -> R.id.pesanan
            FRAGMENT_ACCOUNT -> R.id.account
            else -> R.id.home
        })

        menu_bottom.setOnItemSelectedListener {
            when (it) {
                R.id.home -> openFragment(FRAGMENT_HOME, -1)
                R.id.menu -> openFragment(FRAGMENT_MENU, -1)
                R.id.pesanan -> openFragment(FRAGMENT_HISTORY, -1)
                R.id.account -> openFragment(FRAGMENT_ACCOUNT, -1)
            }
        }
    }

    private fun openFragment(fragmentId: String, tabId: Int) {
        val fragment = when (fragmentId) {
            FRAGMENT_MENU -> ProductFragment().apply {
                arguments = Bundle().apply {
                    putInt(EXTRA_TAB_ID, tabId)
                }
            }
            FRAGMENT_HISTORY -> HistoryFragment()
            FRAGMENT_ACCOUNT -> AccountFragment()
            else -> HomeFragment()
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.frag_container_nav, fragment)
            .commit()
    }
}

