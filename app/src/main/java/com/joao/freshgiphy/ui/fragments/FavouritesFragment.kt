package com.joao.freshgiphy.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar

import com.joao.freshgiphy.R
import com.joao.freshgiphy.models.Gif
import com.joao.freshgiphy.ui.activities.MainActivity
import com.joao.freshgiphy.ui.adapters.FavouritesAdapter
import com.joao.freshgiphy.utils.extensions.removeDialog
import com.joao.freshgiphy.viewmodel.FavouritesViewModel
import kotlinx.android.synthetic.main.fragment_favourites.*

class FavouritesFragment : BaseFragment<FavouritesViewModel>() {

    companion object {
        fun newInstance(): FavouritesFragment {
            return FavouritesFragment()
        }
    }

    override lateinit var viewModel: FavouritesViewModel

    private val favouritesAdapter by lazy { FavouritesAdapter(this, Glide.with(this)) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val factory = (activity as MainActivity).getAppContainer().favouritesViewModelFactory
        viewModel = ViewModelProvider(this, factory).get(FavouritesViewModel::class.java)

        return inflater.inflate(R.layout.fragment_favourites, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()

        viewModel.loadFavourites()
    }

    private fun setupViews() {
        ctnEmpty = emptyView
        ctnList = recyclerView
        ctnLoading = loadingAnim

        val layoutManager = StaggeredGridLayoutManager(favouriteColumns, LinearLayout.VERTICAL).apply {
            gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS
        }

        recyclerView.apply {
            this.adapter = favouritesAdapter
            this.layoutManager = layoutManager
        }
    }

    private fun setupObservers() {
        viewModel.getFavourites().observe(this, Observer { favouritesAdapter.setItems(it) })
    }

    override fun onGifClick(gif: Gif) {
        context?.removeDialog { viewModel.onGifClick(gif) }
    }

    override fun onGifChanged(gif: Gif) {
        if (gif.isFavourite) recyclerView.scrollToPosition(0)
        favouritesAdapter.updateItem(gif)
    }

    override fun displayError(errorMsg: String) {
        Snackbar.make(recyclerView, errorMsg, Snackbar.LENGTH_INDEFINITE)
            .setAction(R.string.retry) { viewModel.loadFavourites() }
            .show()
    }
}
