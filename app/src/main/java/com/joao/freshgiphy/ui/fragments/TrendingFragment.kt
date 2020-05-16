package com.joao.freshgiphy.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager

import com.joao.freshgiphy.R
import com.joao.freshgiphy.api.responses.GifResponse
import com.joao.freshgiphy.di.AppInjector
import com.joao.freshgiphy.models.Gif
import com.joao.freshgiphy.ui.adapters.TrendingAdapter
import com.joao.freshgiphy.viewmodel.TrendingViewModel
import kotlinx.android.synthetic.main.fragment_trending.*

class TrendingFragment : Fragment() {

    private val viewModel by lazy {
        val factory = AppInjector.getTrendingViewModelFactory()
        ViewModelProvider(this, factory).get(TrendingViewModel::class.java)
    }

    private val trendingAdapter by lazy {
        TrendingAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_trending, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        val layoutManager = StaggeredGridLayoutManager(1, LinearLayout.VERTICAL).apply {
            gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS
        }

        recyclerView.apply {
            this.adapter = trendingAdapter
            this.layoutManager = layoutManager
            setHasFixedSize(true)
        }
    }

    private fun setupObservers() {
        with(viewModel) {
            isLoadingEvent.observe(this@TrendingFragment, Observer { showLoading(it) })
            errorEvent.observe(this@TrendingFragment, Observer { showError(it) })
            getGifs().observe(this@TrendingFragment, Observer { showGifs(it) })
        }
    }

    private fun showGifs(gifs: List<Gif>) {
        trendingAdapter.addItems(gifs)
    }

    private fun showLoading(isLoading: Boolean) {
        Log.v("-----", isLoading.toString())
        //TODO
    }

    private fun showError(error: String) {
        Toast.makeText(activity, error, Toast.LENGTH_LONG).show()
    }

}