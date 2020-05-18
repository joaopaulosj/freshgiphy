package com.joao.freshgiphy.ui.fragments

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide

import com.joao.freshgiphy.R
import com.joao.freshgiphy.models.Gif
import com.joao.freshgiphy.ui.activities.MainActivity
import com.joao.freshgiphy.ui.adapters.FavouritesAdapter
import com.joao.freshgiphy.ui.adapters.GifClickListener
import com.joao.freshgiphy.utils.Constants
import com.joao.freshgiphy.utils.extensions.doNothing
import com.joao.freshgiphy.viewmodel.FavouritesViewModel
import kotlinx.android.synthetic.main.fragment_favourites.*
import kotlinx.android.synthetic.main.fragment_favourites.recyclerView

class FavouritesFragment : Fragment(), GifClickListener, FavouritesAdapter.EmptyListListener {

    companion object {
        fun newInstance(): FavouritesFragment {
            return FavouritesFragment()
        }
    }

    private lateinit var viewModel: FavouritesViewModel

    private var columnCount = Constants.FAVOURITE_COLUMNS_PORTRAIT

    private val favouritesAdapter by lazy {
        FavouritesAdapter(this, this, Glide.with(this))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val currentOrientation = resources.configuration.orientation
        columnCount = if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            Constants.FAVOURITE_COLUMNS_PORTRAIT
        } else {
            Constants.FAVOURITE_COLUMNS_LANDSCAPE
        }

        return inflater.inflate(R.layout.fragment_favourites, container, false)
    }

    override fun onAttach(context: Context) {
        val factory = (activity as MainActivity).getAppContainer().favouritesViewModelFactory
        viewModel = ViewModelProvider(this, factory).get(FavouritesViewModel::class.java)
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()

        viewModel.loadFavourites()
    }

    override fun onFavouriteClicked(gif: Gif) {
        context?.let {
            AlertDialog.Builder(it)
                .setTitle(getString(R.string.dialog_remove_title))
                .setMessage(getString(R.string.dialog_remove_message))
                .setPositiveButton(getString(R.string.remove)) { _, _ -> viewModel.onFavouriteClick(gif) }
                .setNegativeButton(getString(R.string.cancel)) { _, _ -> doNothing() }
                .show()
        }
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
        viewModel.onGifChanged().observe(this, Observer {
            if (it.isFavourite) recyclerView.scrollToPosition(0)
            favouritesAdapter.updateItem(it)
        })
    }

    override fun isListEmpty(isEmpty: Boolean) {
        if (isEmpty) {
            recyclerView.visibility = View.GONE
            emptyView.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            emptyView.visibility = View.GONE
        }
    }
}
