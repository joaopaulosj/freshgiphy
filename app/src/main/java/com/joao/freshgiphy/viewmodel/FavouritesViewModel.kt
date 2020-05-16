package com.joao.freshgiphy.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.joao.freshgiphy.repositories.IGiphyRepository

class FavouritesViewModel constructor(private val repository: IGiphyRepository) : ViewModel() {


}

class FavouritesViewModelFactory(
    private val repository: IGiphyRepository
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return FavouritesViewModel(repository) as T
    }
}