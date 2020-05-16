package com.joao.freshgiphy.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

import com.joao.freshgiphy.R
import com.joao.freshgiphy.api.responses.GifResponse
import com.joao.freshgiphy.di.AppInjector
import com.joao.freshgiphy.viewmodel.TrendingViewModel

class TrendingFragment : Fragment() {

    private val viewModel by lazy {
        val factory = AppInjector.getTrendingViewModelFactory()
        ViewModelProvider(this, factory).get(TrendingViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_trending, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
    }

    private fun setupObservers() {
        with(viewModel) {
            isLoadingEvent.observe(this@TrendingFragment, Observer { showLoading(it) })
            errorEvent.observe(this@TrendingFragment, Observer { showError(it) })
            getGifs().observe(this@TrendingFragment, Observer { showGifs(it) })
        }
    }

    private fun showGifs(gifs: List<GifResponse>) {
        //TODO
        Log.v("-----", "${gifs.size} gifs found")
    }

    private fun showLoading(isLoading: Boolean) {
        Log.v("-----", isLoading.toString())
        //TODO
    }

    private fun showError(error: String) {
        Toast.makeText(activity, error, Toast.LENGTH_LONG).show()
    }

}