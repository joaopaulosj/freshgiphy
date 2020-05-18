package com.joao.freshgiphy.ui.fragments

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagedList
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.jakewharton.rxbinding.widget.RxTextView
import com.joao.freshgiphy.R
import com.joao.freshgiphy.models.Gif
import com.joao.freshgiphy.models.ListStatus
import com.joao.freshgiphy.models.Status
import com.joao.freshgiphy.ui.activities.MainActivity
import com.joao.freshgiphy.ui.adapters.GifClickListener
import com.joao.freshgiphy.ui.adapters.TrendingPagedAdapter
import com.joao.freshgiphy.utils.Constants
import com.joao.freshgiphy.utils.extensions.doNothing
import com.joao.freshgiphy.utils.extensions.hideKeyboard
import com.joao.freshgiphy.utils.extensions.showKeyboard
import com.joao.freshgiphy.viewmodel.TrendingViewModel
import kotlinx.android.synthetic.main.fragment_trending.*
import rx.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit


class TrendingFragment : Fragment(), GifClickListener {

    companion object {
        fun newInstance(): TrendingFragment {
            return TrendingFragment()
        }
    }

    private lateinit var viewModel: TrendingViewModel

    private var columnCount = Constants.TRENDING_COLUMNS_PORTRAIT

    private val trendingPagedAdapter by lazy {
        TrendingPagedAdapter(this, Glide.with(this))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val currentOrientation = resources.configuration.orientation
        columnCount = if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            Constants.TRENDING_COLUMNS_PORTRAIT
        } else {
            Constants.TRENDING_COLUMNS_LANDSCAPE
        }

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
        swipeRefresh.setOnRefreshListener { viewModel.refresh() }

        val layoutManager = StaggeredGridLayoutManager(columnCount, LinearLayout.VERTICAL).apply {
            gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS
        }

        recyclerView.apply {
            this.adapter = trendingPagedAdapter
            this.layoutManager = layoutManager
            itemAnimator = null
        }

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

    private fun onSearchChange(text: String) {
        viewModel.onSearchQuery(text)

        clearSearchBtn.visibility = if (text.isBlank()) {
            View.INVISIBLE
        } else {
            View.VISIBLE
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
        if (gif.isFavourite) {
            context?.let {
                AlertDialog.Builder(it)
                    .setTitle(getString(R.string.dialog_remove_title))
                    .setMessage(getString(R.string.dialog_remove_message))
                    .setPositiveButton(getString(R.string.remove)) { _, _ -> viewModel.onFavouriteClick(gif) }
                    .setNegativeButton(getString(R.string.cancel)) { _, _ -> doNothing() }
                    .show()
            }
        } else {
            viewModel.onFavouriteClick(gif)
        }
    }

    private fun setupObservers() {
        viewModel.getNetworkState().observe(this, Observer { trendingPagedAdapter.setNetworkState(it) })
        viewModel.onGifChanged().observe(this, Observer { trendingPagedAdapter.updateItem(it) })
        viewModel.getGifs().observe(this, Observer { onPageListLoaded(it) })
        viewModel.listStatusEvent().observe(this, Observer { updateListStatus(it) })
    }

    private fun onPageListLoaded(list: PagedList<Gif>) {
        trendingPagedAdapter.submitList(list)
        swipeRefresh.isRefreshing = false
    }

    private fun displayError(errorMsg: String) {
        Snackbar.make(recyclerView, errorMsg, Snackbar.LENGTH_INDEFINITE)
            .setAction(R.string.retry) { viewModel.refresh() }
            .show()
    }

    private fun updateListStatus(listStatus: ListStatus) {
        when (listStatus.status) {
            Status.LOADING -> {
                loadingAnim.visibility = View.VISIBLE
                emptyView.visibility = View.GONE
                recyclerView.visibility = View.GONE
            }
            Status.EMPTY -> {
                loadingAnim.visibility = View.GONE
                emptyView.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            }
            Status.ERROR -> {
                displayError(listStatus.message ?: getString(R.string.unknown_error))
                loadingAnim.visibility = View.GONE
                emptyView.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            }
            Status.DEFAULT -> {
                loadingAnim.visibility = View.GONE
                emptyView.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }
        }
    }

}