package com.joao.freshgiphy.ui.fragments

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.joao.freshgiphy.R
import com.joao.freshgiphy.models.Gif
import com.joao.freshgiphy.models.ListStatus
import com.joao.freshgiphy.models.Status
import com.joao.freshgiphy.ui.adapters.GifClickListener
import com.joao.freshgiphy.utils.Constants
import com.joao.freshgiphy.viewmodel.BaseViewModel

abstract class BaseFragment<T : BaseViewModel> : Fragment(), GifClickListener {

    protected abstract val viewModel: T

    protected var ctnList: View? = null
    protected var ctnEmpty: View? = null
    protected var ctnLoading: View? = null

    protected var trendingColumns: Int = Constants.TRENDING_COLUMNS_PORTRAIT
    protected var favouriteColumns: Int = Constants.FAVOURITE_COLUMNS_PORTRAIT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        retainInstance = true

//        val currentOrientation = resources.configuration.orientation
//        if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
//            trendingColumns = Constants.TRENDING_COLUMNS_PORTRAIT
//            favouriteColumns = Constants.FAVOURITE_COLUMNS_PORTRAIT
//        } else {
//            trendingColumns = Constants.TRENDING_COLUMNS_LANDSCAPE
//            favouriteColumns = Constants.FAVOURITE_COLUMNS_LANDSCAPE
//        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
    }

    private fun setupObservers() {
        viewModel.listStatusEvent().observe(this, Observer { updateListStatus(it) })
        viewModel.gifChangedEvent().observe(this, Observer { onGifChanged(it) })
    }

    protected abstract fun displayError(errorMsg: String)

    protected abstract fun onGifChanged(gif: Gif)

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