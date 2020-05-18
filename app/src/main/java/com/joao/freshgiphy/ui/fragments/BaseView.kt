package com.joao.freshgiphy.ui.fragments

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.joao.freshgiphy.R
import com.joao.freshgiphy.models.Gif
import com.joao.freshgiphy.models.ListStatus
import com.joao.freshgiphy.models.Status
import com.joao.freshgiphy.ui.adapters.FavouritesAdapter.StatusListener
import com.joao.freshgiphy.ui.adapters.GifClickListener
import com.joao.freshgiphy.utils.Constants
import com.joao.freshgiphy.utils.extensions.doNothing
import com.joao.freshgiphy.viewmodel.BaseViewModel

/*
 * The base class was created to share some properties and behaviours that
 * are common between the project's fragment views, such as setting the columns count,
 * toggling favourites, showing loading, empty, error and success status
 */
abstract class BaseView<T : BaseViewModel> : Fragment(), GifClickListener, StatusListener {

    protected abstract val viewModel: T

    protected var ctnList: View? = null
    protected var ctnEmpty: View? = null
    protected var ctnLoading: View? = null

    protected var trendingColumns: Int = Constants.TRENDING_COLUMNS_PORTRAIT
    protected var favouriteColumns: Int = Constants.FAVOURITE_COLUMNS_PORTRAIT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val currentOrientation = resources.configuration.orientation
        if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            trendingColumns = Constants.TRENDING_COLUMNS_PORTRAIT
            favouriteColumns = Constants.FAVOURITE_COLUMNS_PORTRAIT
        } else {
            trendingColumns = Constants.TRENDING_COLUMNS_LANDSCAPE
            favouriteColumns = Constants.FAVOURITE_COLUMNS_LANDSCAPE
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
    }

    private fun setupObservers() {
        viewModel.listStatusEvent.observe(this, Observer { updateListStatus(it) })
        viewModel.gifChangedEvent.observe(this, Observer { onGifChanged(it) })
    }

    override fun onGifClick(gif: Gif) {
        if (gif.isFavourite) {
            // If user is removing gif from favourites,
            // only call viewModel after dialog is confirmed
            AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.dialog_remove_title))
                .setMessage(getString(R.string.dialog_remove_message))
                .setPositiveButton(getString(R.string.remove)) { _, _ -> viewModel.onGifClick(gif) }
                .setNegativeButton(getString(R.string.cancel)) { _, _ -> doNothing() }
                .show()
        } else {
            // It doesn't need confirmation to add to favourites
            viewModel.onGifClick(gif)
        }
    }

    protected abstract fun displayError(errorMsg: String)

    protected abstract fun onGifChanged(gif: Gif)

    override fun onStatusChanged(status: ListStatus) = updateListStatus(status)

    private fun updateListStatus(listStatus: ListStatus) {
        when (listStatus.status) {
            Status.LOADING -> {
                ctnLoading?.visibility = View.VISIBLE
                ctnEmpty?.visibility = View.GONE
                ctnList?.visibility = View.GONE
            }
            Status.EMPTY -> {
                ctnLoading?.visibility = View.GONE
                ctnEmpty?.visibility = View.VISIBLE
                ctnList?.visibility = View.GONE
            }
            Status.ERROR -> {
                displayError(listStatus.message ?: getString(R.string.unknown_error))
                ctnLoading?.visibility = View.GONE
                ctnEmpty?.visibility = View.VISIBLE
                ctnList?.visibility = View.GONE
            }
            Status.SUCCESS -> {
                ctnLoading?.visibility = View.GONE
                ctnEmpty?.visibility = View.GONE
                ctnList?.visibility = View.VISIBLE
            }
        }
    }

}