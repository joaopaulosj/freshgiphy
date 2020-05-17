package com.joao.freshgiphy.ui.fragments

import android.content.Context
import android.opengl.Visibility
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.joao.freshgiphy.R
import com.joao.freshgiphy.di.App
import com.joao.freshgiphy.models.Gif
import com.joao.freshgiphy.ui.adapters.GifClickListener
import com.joao.freshgiphy.ui.adapters.TrendingPagedAdapter
import com.joao.freshgiphy.utils.Constants
import com.joao.freshgiphy.utils.extensions.addTextWatcherDebounce
import com.joao.freshgiphy.viewmodel.TrendingViewModel
import kotlinx.android.synthetic.main.fragment_trending.*

class TrendingFragment : Fragment(), GifClickListener {

    private lateinit var viewModel: TrendingViewModel

    private val trendingPagedAdapter by lazy {
        TrendingPagedAdapter(this, Glide.with(this))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_trending, container, false)
    }

    override fun onAttach(context: Context) {
        val factory = (activity?.application as App).appContainer.trendingViewModelFactory
        viewModel = ViewModelProvider(this, factory).get(TrendingViewModel::class.java) //TODO pass to activity

        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        recyclerView.apply {
            this.adapter = trendingPagedAdapter
            this.layoutManager = LinearLayoutManager(activity)
            itemAnimator = null
        }

        searchEdt.addTextWatcherDebounce(Constants.EDIT_TEXT_DEBOUNCE_TIME) { viewModel.search(it) }

        swipeRefresh.setOnRefreshListener { viewModel.refresh() }
    }

    override fun onFavouriteClicked(gif: Gif) {
        viewModel.onFavouriteClick(gif)
    }

    private fun setupObservers() {
        viewModel.getNetworkState().observe(this, Observer { trendingPagedAdapter.setNetworkState(it) })
        viewModel.onGifChanged().observe(this, Observer { trendingPagedAdapter.updateItem(it) })
        viewModel.getGifs().observe(this, Observer {
            trendingPagedAdapter.submitList(it)
            swipeRefresh.isRefreshing = false

            if (loadingAnim.visibility == View.VISIBLE) {
                loadingAnim.visibility = View.GONE
                loadingAnim.cancelAnimation()
                recyclerView.visibility = View.VISIBLE
            }
        })
    }

}