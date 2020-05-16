package com.joao.freshgiphy.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider

import com.joao.freshgiphy.R
import com.joao.freshgiphy.di.AppInjector
import com.joao.freshgiphy.viewmodel.FavouritesViewModel
import com.joao.freshgiphy.viewmodel.TrendingViewModel

class FavouritesFragment : Fragment() {

    private val viewModel by lazy {
        val factory = AppInjector.getFavouritesViewModelFactory()
        ViewModelProvider(this, factory).get(FavouritesViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_trending, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

}
