package com.joao.freshgiphy.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagedList
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.jakewharton.rxbinding.widget.RxTextView
import com.joao.freshgiphy.R
import com.joao.freshgiphy.models.Gif
import com.joao.freshgiphy.ui.activities.MainActivity
import com.joao.freshgiphy.ui.adapters.TrendingPagedAdapter
import com.joao.freshgiphy.utils.Constants
import com.joao.freshgiphy.utils.extensions.hideKeyboard
import com.joao.freshgiphy.utils.extensions.showKeyboard
import com.joao.freshgiphy.viewmodel.TrendingViewModel
import kotlinx.android.synthetic.main.fragment_trending.*
import kotlinx.android.synthetic.main.fragment_trending.emptyView
import kotlinx.android.synthetic.main.fragment_trending.loadingAnim
import kotlinx.android.synthetic.main.fragment_trending.recyclerView
import rx.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit


class TrendingFragment : BaseView<TrendingViewModel>() {

    companion object {
        fun newInstance(): TrendingFragment {
            return TrendingFragment()
        }
    }

    override lateinit var viewModel: TrendingViewModel

    private val trendingPagedAdapter by lazy {
        TrendingPagedAdapter(this, Glide.with(this))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val factory = (requireActivity() as MainActivity).getAppContainer().trendingViewModelFactory
        viewModel = ViewModelProvider(this, factory).get(TrendingViewModel::class.java)
        return inflater.inflate(R.layout.fragment_trending, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        ctnEmpty = emptyView
        ctnList = swipeRefresh
        ctnLoading = loadingAnim

        setupList()
        setupClearField()
    }

    private fun setupList() {
        val layoutManager = StaggeredGridLayoutManager(trendingColumns, LinearLayout.VERTICAL).apply {
            gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS
        }

        recyclerView.apply {
            this.adapter = trendingPagedAdapter
            this.layoutManager = layoutManager
            itemAnimator = null
        }

        swipeRefresh.setOnRefreshListener {
            viewModel.refresh()
            swipeRefresh.isRefreshing = false
        }
    }

    private fun onSearchChange(text: String) {
        viewModel.onSearchQuery(text)

        clearSearchBtn.visibility = if (text.isBlank()) {
            View.INVISIBLE
        } else {
            View.VISIBLE
        }
    }

    private fun setupClearField() {
        clearSearchBtn.setOnClickListener { onClearSearchClicked() }

        RxTextView.textChanges(searchEdt)
            .debounce(Constants.EDIT_TEXT_DEBOUNCE_TIME, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { onSearchChange(it.toString()) }

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

    private fun setupObservers() {
        viewModel.getNetworkState().observe(this, Observer { trendingPagedAdapter.setNetworkState(it) })
        viewModel.getGifs().observe(this, Observer { onPageListLoaded(it) })
    }

    private fun onPageListLoaded(list: PagedList<Gif>) {
        trendingPagedAdapter.submitList(list)
    }

    override fun onGifChanged(gif: Gif) {
        trendingPagedAdapter.updateItem(gif)
    }

    override fun displayError(errorMsg: String) {
        Snackbar.make(recyclerView, errorMsg, Snackbar.LENGTH_INDEFINITE)
            .setAction(R.string.retry) { viewModel.refresh() }
            .show()
    }

}