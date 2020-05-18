package com.joao.freshgiphy

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.Observer
import com.joao.freshgiphy.models.Gif
import com.joao.freshgiphy.models.ListStatus
import com.joao.freshgiphy.models.Status
import com.joao.freshgiphy.repositories.GiphyRepository
import com.joao.freshgiphy.viewmodel.FavouritesViewModel
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

@RunWith(MockitoJUnitRunner::class)
class FavouritesViewModelTest : LifecycleOwner {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Mock
    private lateinit var repository: GiphyRepository

    @Mock
    private lateinit var favouritesObserver: Observer<List<Gif>>

    @Mock
    private lateinit var listStatusObserver: Observer<ListStatus>

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
        viewModel.listStatusEvent.observeForever(listStatusObserver)
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
        viewModel.onGifClick(gif)

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
        verify(listStatusObserver).onChanged(ListStatus(Status.LOADING))
        verify(listStatusObserver).onChanged(ListStatus(Status.SUCCESS))
        verify(favouritesObserver).onChanged(gifsListMock)
    }

    @Test
    fun `WHEN load favourites and gets error THEN should update status to error`() {
        // Configuration
        whenever(repository.getFavourites()).thenReturn(Single.error(Throwable("Error")))
        viewModel.getFavourites().observeForever(favouritesObserver)

        // Execution
        viewModel.loadFavourites()

        testScheduler.triggerActions()

        // Assertion
        verify(listStatusObserver).onChanged(ListStatus(Status.LOADING))
        verify(listStatusObserver).onChanged(ListStatus(Status.ERROR, "Error"))
    }

    @Test
    fun `WHEN load favourites and gets empty list THEN should update status to empty`() {
        // Configuration
        whenever(repository.getFavourites()).thenReturn(Single.just(emptyList()))
        viewModel.getFavourites().observeForever(favouritesObserver)

        // Execution
        viewModel.loadFavourites()

        testScheduler.triggerActions()

        // Assertion
        verify(listStatusObserver).onChanged(ListStatus(Status.LOADING))
        verify(listStatusObserver).onChanged(ListStatus(Status.EMPTY))
    }

}
