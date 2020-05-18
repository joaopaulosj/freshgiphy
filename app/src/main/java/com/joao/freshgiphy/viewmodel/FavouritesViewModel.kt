package com.joao.freshgiphy.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.joao.freshgiphy.models.Gif
import com.joao.freshgiphy.repositories.IGiphyRepository
import com.joao.freshgiphy.utils.SingleLiveEvent
import com.joao.freshgiphy.utils.extensions.rxSubscribe

class FavouritesViewModel constructor(private val repository: IGiphyRepository) : ViewModel() {

    private val favouritesLiveData = MutableLiveData<List<Gif>>()
        .also { loadFavourites() }

    private fun loadFavourites() {
        repository.getFavourites()
            .rxSubscribe(onSuccess = {
                favouritesLiveData.postValue(it)
            }, onError = {
                //TODO
            })
    }

    fun getFavourites(): LiveData<List<Gif>> = favouritesLiveData

    fun onFavouriteGifChanged(): SingleLiveEvent<Gif> = repository.onFavouriteGifChanged()

    fun onFavouriteClick(gif: Gif) = repository.toggleFavourite(gif)
}

class FavouritesViewModelFactory(
    private val repository: IGiphyRepository
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return FavouritesViewModel(repository) as T
    }
}