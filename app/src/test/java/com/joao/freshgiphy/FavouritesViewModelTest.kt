package com.joao.freshgiphy

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.Observer
import com.joao.freshgiphy.api.responses.ApiResponse
import com.joao.freshgiphy.api.responses.GifImage
import com.joao.freshgiphy.api.responses.GifPreview
import com.joao.freshgiphy.api.responses.GifResponse
import com.joao.freshgiphy.api.responses.MetaResponse
import com.joao.freshgiphy.api.responses.PaginationResponse
import com.joao.freshgiphy.models.Gif
import com.joao.freshgiphy.models.ListStatus
import com.joao.freshgiphy.models.Status
import com.joao.freshgiphy.models.toGif
import com.joao.freshgiphy.repositories.GiphyRepository
import com.joao.freshgiphy.ui.datasource.TrendingDataFactory
import com.joao.freshgiphy.utils.extensions.rxSubscribe
import com.joao.freshgiphy.viewmodel.FavouritesViewModel
import com.joao.freshgiphy.viewmodel.TrendingViewModel
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Single
import io.reactivex.schedulers.TestScheduler
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import java.util.concurrent.TimeUnit

@RunWith(MockitoJUnitRunner::class)
class FavouritesViewModelTest : LifecycleOwner {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Mock
    private lateinit var repository: GiphyRepository

    @Mock
    private lateinit var favouritesObserver: Observer<List<Gif>>

    private val lifecycle by lazy { LifecycleRegistry(this) }

    private lateinit var viewModel: FavouritesViewModel
    private lateinit var testScheduler: TestScheduler
    private lateinit var gifsListMock: List<Gif>

    @Before
    fun setup() {
        initMocks()
        MockitoAnnotations.initMocks(this)

        testScheduler = TestScheduler()

        viewModel = spy(FavouritesViewModel(repository, testScheduler, testScheduler))
    }

    private fun initMocks() {
        gifsListMock = listOf(Gif(id = "id1", url = "url1"), Gif(id = "id2", url = "url2"))
    }

    override fun getLifecycle(): Lifecycle {
        return lifecycle
    }

    @Test
    fun `WHEN a gif is clicked THEN should call toggle favourite in repository`() {
        // Configuration
        val gif = Gif(id = "id", url = "url", isFavourite = false)

        // Execution
        viewModel.onFavouriteClick(gif)

        // Assertion
        verify(repository, times(1)).toggleFavourite(gif)
    }

    @Test
    fun `WHEN load favourites THEN should pass the data to favourites livedata`() {
        // Configuration
        whenever(repository.getFavourites()).thenReturn(Single.just(gifsListMock))
        viewModel.getFavourites().observeForever(favouritesObserver)

        // Execution
        viewModel.loadFavourites()

        testScheduler.triggerActions()

        // Assertion
        verify(favouritesObserver).onChanged(gifsListMock)
    }

}
