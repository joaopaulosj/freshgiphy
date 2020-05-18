package com.joao.freshgiphy.ui.fragments

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
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
import com.joao.freshgiphy.models.ListStatus
import com.joao.freshgiphy.models.Status
import com.joao.freshgiphy.ui.activities.MainActivity
import com.joao.freshgiphy.ui.adapters.FavouritesAdapter
import com.joao.freshgiphy.ui.adapters.GifClickListener
import com.joao.freshgiphy.utils.Constants
import com.joao.freshgiphy.utils.extensions.removeDialog
import com.joao.freshgiphy.viewmodel.FavouritesViewModel
import kotlinx.android.synthetic.main.fragment_favourites.emptyView
import kotlinx.android.synthetic.main.fragment_favourites.recyclerView

class FavouritesFragment : Fragment(), GifClickListener {

    companion object {
        fun newInstance(): FavouritesFragment {
            return FavouritesFragment()
        }
    }

    private lateinit var viewModel: FavouritesViewModel

    private var columnCount = Constants.FAVOURITE_COLUMNS_PORTRAIT

    private val favouritesAdapter by lazy {
        FavouritesAdapter(this, Glide.with(this))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val currentOrientation = resources.configuration.orientation
        columnCount = if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            Constants.FAVOURITE_COLUMNS_PORTRAIT
        } else {
            Constants.FAVOURITE_COLUMNS_LANDSCAPE
        }

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

    override fun onFavouriteClicked(gif: Gif) {
        context?.removeDialog { viewModel.onFavouriteClick(gif) }
    }

    private fun setupViews() {
        val layoutManager = StaggeredGridLayoutManager(columnCount, LinearLayout.VERTICAL).apply {
            gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS
        }

        recyclerView.apply {
            this.adapter = favouritesAdapter
            this.layoutManager = layoutManager
        }
    }

    private fun setupObservers() {
        viewModel.getFavourites().observe(this, Observer { favouritesAdapter.setItems(it) })
        viewModel.getListStatus().observe(this, Observer { updateListStatus(it) })
        viewModel.onGifChanged().observe(this, Observer {
            if (it.isFavourite) recyclerView.scrollToPosition(0)
            favouritesAdapter.updateItem(it)
        })
    }

    private fun updateListStatus(listStatus: ListStatus) {
        when (listStatus.status) {
            Status.LOADING -> {
//                loadingAnim.visibility = View.VISIBLE
                emptyView.visibility = View.GONE
                recyclerView.visibility = View.GONE
            }
            Status.EMPTY -> {
//                loadingAnim.visibility = View.GONE
                emptyView.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            }
            Status.ERROR -> {
                displayError(listStatus.message ?: getString(R.string.unknown_error))
//                loadingAnim.visibility = View.GONE
                emptyView.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            }
            Status.SUCCESS -> {
//                loadingAnim.visibility = View.GONE
                emptyView.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }
        }
    }

    private fun displayError(errorMsg: String) {
        Snackbar.make(recyclerView, errorMsg, Snackbar.LENGTH_INDEFINITE)
            .setAction(R.string.retry) { viewModel.loadFavourites() }
            .show()
    }
}
