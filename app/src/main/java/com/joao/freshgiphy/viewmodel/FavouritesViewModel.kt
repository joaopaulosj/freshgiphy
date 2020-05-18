package com.joao.freshgiphy.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.joao.freshgiphy.models.Gif
import com.joao.freshgiphy.models.ListStatus
import com.joao.freshgiphy.models.Status
import com.joao.freshgiphy.repositories.IGiphyRepository
import com.joao.freshgiphy.utils.SingleLiveEvent
import com.joao.freshgiphy.utils.extensions.rxSubscribe
import io.reactivex.Scheduler

class FavouritesViewModel constructor(
    private val repository: IGiphyRepository,
    private val processScheduler: Scheduler,
    private val androidScheduler: Scheduler
) : BaseViewModel() {

    private val favouritesLiveData = MutableLiveData<List<Gif>>()

    override val gifChangedEvent = repository.favouriteChangeEvent

    fun loadFavourites() {
        listStatusEvent.postValue(ListStatus(Status.LOADING))

        repository.getFavourites()
            .rxSubscribe(
                subscribeOnScheduler = processScheduler,
                observeOnScheduler = androidScheduler,
                onSuccess = {
                    if (it.isNotEmpty()) {
                        listStatusEvent.postValue(ListStatus(Status.SUCCESS))
                    } else {
                        listStatusEvent.postValue(ListStatus(Status.EMPTY))
                    }

                    favouritesLiveData.postValue(it)
                }, onError = {
                    listStatusEvent.postValue(ListStatus(Status.ERROR, it.message))
                })
    }

    fun getFavourites(): LiveData<List<Gif>> = favouritesLiveData

    fun onGifClick(gif: Gif) = repository.toggleFavourite(gif)
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