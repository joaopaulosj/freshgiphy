package com.joao.freshgiphy.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.joao.freshgiphy.R
import com.joao.freshgiphy.ui.fragments.FavouritesFragment
import com.joao.freshgiphy.ui.fragments.TrendingFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupTabs()
    }

    private fun setupTabs() {
        val fragments = listOf(TrendingFragment(), FavouritesFragment())

        viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun createFragment(position: Int): Fragment {
                return fragments[position]
            }

            override fun getItemCount() = 2
        }

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = if(position == 0){
                "TRENDING"
            } else {
                "FAVOURITES"
            }
        }.attach()
    }

}