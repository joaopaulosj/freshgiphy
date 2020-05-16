package com.joao.freshgiphy.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.joao.freshgiphy.models.Gif
import com.joao.freshgiphy.repositories.IGiphyRepository
import com.joao.freshgiphy.utils.extensions.singleSubscribe

class FavouritesViewModel constructor(repository: IGiphyRepository) : ViewModel() {

    private val favsLiveData = repository.getFavourites()

    fun getFavourites(): LiveData<List<Gif>> = favsLiveData
}

class FavouritesViewModelFactory(
    private val repository: IGiphyRepository
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return FavouritesViewModel(repository) as T
    }
}