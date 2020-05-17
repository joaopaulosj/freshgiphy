package com.joao.freshgiphy.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.joao.freshgiphy.models.Gif
import com.joao.freshgiphy.repositories.IGiphyRepository

class FavouritesViewModel constructor(private val repository: IGiphyRepository) : ViewModel() {

    private val favsLiveData = repository.getFavourites()

    fun getFavourites(): LiveData<List<Gif>> = favsLiveData

    fun onFavouriteClick(gif: Gif) = repository.toggleFavourite(gif)
}

class FavouritesViewModelFactory(
    private val repository: IGiphyRepository
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return FavouritesViewModel(repository) as T
    }
}