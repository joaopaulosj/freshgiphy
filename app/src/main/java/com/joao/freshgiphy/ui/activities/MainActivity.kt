package com.joao.freshgiphy.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.joao.freshgiphy.R
import com.joao.freshgiphy.di.App
import com.joao.freshgiphy.ui.fragments.FavouritesFragment
import com.joao.freshgiphy.ui.fragments.TrendingFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupTabs()
    }

    private fun setupTabs() {
        val tabNames = listOf(getString(R.string.main_trending_title), getString(R.string.main_favourites_title))
        val fragments = listOf<Fragment>(TrendingFragment.newInstance(), FavouritesFragment.newInstance())

        val primaryColor = ContextCompat.getColor(this, R.color.colorPrimary)
        val secondaryColor = ContextCompat.getColor(this, R.color.colorSecondary)

        // Change background between primary to secondary colors as the tabs are swiped
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)

                val progress = if (positionOffset > 0) positionOffset else position.toFloat()

                secondaryBgView.alpha = progress
                window.statusBarColor = ColorUtils.blendARGB(primaryColor, secondaryColor, progress)
            }
        })

        viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun createFragment(position: Int) = fragments[position]

            override fun getItemCount() = fragments.size
        }

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabNames[position]
        }.attach()
    }

    fun getAppContainer() = (applicationContext as App).appContainer

}