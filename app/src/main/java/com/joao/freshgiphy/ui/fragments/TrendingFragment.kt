package com.joao.freshgiphy.ui.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.joao.freshgiphy.R
import com.joao.freshgiphy.di.App
import com.joao.freshgiphy.models.Gif
import com.joao.freshgiphy.ui.activities.MainActivity
import com.joao.freshgiphy.ui.adapters.GifClickListener
import com.joao.freshgiphy.ui.adapters.TrendingPagedAdapter
import com.joao.freshgiphy.utils.Constants
import com.joao.freshgiphy.utils.extensions.addTextWatcherDebounce
import com.joao.freshgiphy.utils.extensions.hideKeyboard
import com.joao.freshgiphy.utils.extensions.showKeyboard
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
        swipeRefresh.setOnRefreshListener { viewModel.refresh() }

        recyclerView.apply {
            this.adapter = trendingPagedAdapter
            this.layoutManager = LinearLayoutManager(activity)
            itemAnimator = null
        }

        clearSearchBtn.setOnClickListener { onClearSearchClicked() }

        searchEdt.addTextWatcherDebounce(Constants.EDIT_TEXT_DEBOUNCE_TIME) {
            viewModel.search(it)
            loadingAnim.playAnimation()

            clearSearchBtn.visibility = if (it.isBlank()) {
                View.INVISIBLE
            } else {
                View.VISIBLE
            }
        }

        searchEdt.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchEdt.clearFocus()
                searchEdt.hideKeyboard()
            }

            false
        }
    }

    private fun onClearSearchClicked() {
        clearSearchBtn.visibility = View.INVISIBLE

        searchEdt.apply {
            setText("")
            requestFocus()
            showKeyboard()
        }
    }

    override fun onFavouriteClicked(gif: Gif) {
        viewModel.onFavouriteClick(gif)
    }

    private fun setupObservers() {
        viewModel.getNetworkState().observe(this, Observer { trendingPagedAdapter.setNetworkState(it) })
        viewModel.onGifChanged().observe(this, Observer { trendingPagedAdapter.updateItem(it) })
        viewModel.getGifs().observe(this, Observer { onPageListLoaded(it) })
        viewModel.getIsListEmpty().observe(this, Observer { onListIsEmpty(it) })
    }

    private fun onPageListLoaded(list: PagedList<Gif>) {
        trendingPagedAdapter.submitList(list)
        swipeRefresh.isRefreshing = false

        if (loadingAnim.visibility == View.VISIBLE) {
            loadingAnim.visibility = View.GONE
            loadingAnim.cancelAnimation()
        }
    }

    private fun onListIsEmpty(isEmpty: Boolean) {
        if (isEmpty) {
            recyclerView.visibility = View.GONE
            emptyAnim.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            emptyAnim.visibility = View.GONE
        }
    }

}