package com.joao.freshgiphy.ui.fragments

import android.content.Context
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

import com.joao.freshgiphy.R
import com.joao.freshgiphy.di.App
import com.joao.freshgiphy.models.Gif
import com.joao.freshgiphy.ui.adapters.FavouritesAdapter
import com.joao.freshgiphy.ui.adapters.GifClickListener
import com.joao.freshgiphy.viewmodel.FavouritesViewModel
import kotlinx.android.synthetic.main.fragment_favourites.*

class FavouritesFragment : Fragment(), GifClickListener {

    private lateinit var viewModel: FavouritesViewModel

    private val favouritesAdapter by lazy {
        FavouritesAdapter(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_favourites, container, false)
    }

    override fun onAttach(context: Context) {
        val factory = (activity?.application as App).appContainer.favouritesViewModelFactory
        viewModel = ViewModelProvider(this, factory).get(FavouritesViewModel::class.java)
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }

    override fun onFavClicked(gif: Gif) {
        viewModel.onFavClick(gif)
    }

    private fun setupViews() {
        val layoutManager = StaggeredGridLayoutManager(2, LinearLayout.VERTICAL).apply {
            gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS
        }

        recyclerView.apply {
            this.adapter = favouritesAdapter
            this.layoutManager = layoutManager
        }
    }

    private fun setupObservers() {
        viewModel.getFavourites().observe(this, Observer {
            Log.i("-----", it.size.toString())
            favouritesAdapter.setItems(it)
        })
    }

}
