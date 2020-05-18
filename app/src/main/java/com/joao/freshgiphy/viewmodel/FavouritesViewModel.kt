package com.joao.freshgiphy.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.joao.freshgiphy.models.Gif
import com.joao.freshgiphy.repositories.IGiphyRepository
import com.joao.freshgiphy.utils.SingleLiveEvent
import com.joao.freshgiphy.utils.extensions.rxSubscribe
import io.reactivex.Scheduler

class FavouritesViewModel constructor(
    private val repository: IGiphyRepository,
    private val processScheduler: Scheduler,
    private val androidScheduler: Scheduler
) : ViewModel() {

    private val favouritesLiveData = MutableLiveData<List<Gif>>()

    fun loadFavourites() {
        repository.getFavourites()
            .rxSubscribe(
                subscribeOnScheduler = processScheduler,
                observeOnScheduler = androidScheduler,
                onSuccess = {
                    favouritesLiveData.postValue(it)
                })
    }

    fun getFavourites(): LiveData<List<Gif>> = favouritesLiveData

    fun onGifChanged(): SingleLiveEvent<Gif> = repository.favouriteChangeEvent

    fun onFavouriteClick(gif: Gif) {
        repository.toggleFavourite(gif)
    }
}

class FavouritesViewModelFactory(
    private val repository: IGiphyRepository,
    private val processScheduler: Scheduler,
    private val androidScheduler: Scheduler
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return FavouritesViewModel(repository, processScheduler, androidScheduler) as T
    }
}