package com.joao.freshgiphy

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.Observer
import androidx.paging.PagedList
import com.joao.freshgiphy.api.responses.ApiResponse
import com.joao.freshgiphy.api.responses.GifImage
import com.joao.freshgiphy.api.responses.GifPreview
import com.joao.freshgiphy.api.responses.GifResponse
import com.joao.freshgiphy.api.responses.MetaResponse
import com.joao.freshgiphy.api.responses.PaginationResponse
import com.joao.freshgiphy.models.Gif
import com.joao.freshgiphy.repositories.GiphyRepository
import com.joao.freshgiphy.ui.datasource.TrendingDataFactory
import com.joao.freshgiphy.utils.SingleLiveEvent
import com.joao.freshgiphy.viewmodel.TrendingViewModel
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Single
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class TrendingViewModelTest : LifecycleOwner {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val lifecycle by lazy { LifecycleRegistry(this) }

    private lateinit var viewModel: TrendingViewModel

    private lateinit var repository: GiphyRepository

    @Mock
    private lateinit var trendingFactory: TrendingDataFactory

    @Mock
    private lateinit var gifsPagedListObserver: Observer<PagedList<Gif>>

    @Mock
    private lateinit var gifChangedObserver: Observer<Gif>

    private lateinit var apiResponseMock: ApiResponse

    @Before
    fun setup() {
        initMocks()
        MockitoAnnotations.initMocks(this)

        whenever(repository.onTrendingGifChanged()).thenReturn(SingleLiveEvent())

        viewModel = spy(TrendingViewModel(repository, trendingFactory))
        viewModel.getGifs().observe(this, gifsPagedListObserver)
        viewModel.onGifChanged().observe(this, gifChangedObserver)
    }

    private fun initMocks() {
        val gifImage = GifImage(downsized = GifPreview("url", 0, 0))
        val gifResponse = GifResponse(id = "id", url = "url", images = gifImage)
        val data = listOf(gifResponse)
        val pagination = PaginationResponse(1, 1, 0)
        val meta = MetaResponse(200, "")
        apiResponseMock = ApiResponse(data, pagination, meta)
    }

    override fun getLifecycle(): Lifecycle {
        return lifecycle
    }

    @Test
    fun `WHEN do first gifs request and receives data THEN should pass it to gifsLiveData`() {
        // Configuration
        whenever(repository.getTrending(any())).thenReturn(Single.just(apiResponseMock))

        // Execution
        repository.getTrending(0)

        // Assertion
        verify(gifsPagedListObserver).onChanged(any())
    }

    @Test
    fun `WHEN do first gifs request and receives no data THEN should pass it to gifsLiveData`() {
        // Configuration

        // Execution

        // Assertion
    }

    @Test
    fun `WHEN do first gifs request and receives an error THEN should show error`() {
        // Configuration

        // Execution

        // Assertion
    }

    @Test
    fun `WHEN a gif is favorited THEN should update item in list`() {
        // Configuration
        val gif = Gif(id = "id", url = "url", isFavourite = false)

        // Execution
        viewModel.onFavouriteClick(gif)

        // Assertion
        verify(gifChangedObserver).onChanged(gif)
    }

    @Test
    fun `WHEN a gif is unfavorited THEN should update item in list`() {
        // Configuration

        // Execution

        // Assertion
    }

    @Test
    fun `WHEN refresh is called THEN should repeat last request`() {
        // Configuration

        // Execution

        // Assertion
    }

    @Test
    fun `WHEN a search is entered THEN should do search request with text`() {
        // Configuration

        // Execution

        // Assertion
    }

    @Test
    fun `WHEN a search doesnt return results THEN should show empty list`() {
        // Configuration

        // Execution

        // Assertion
    }

    @Test
    fun `WHEN a search returns an error THEN should show error`() {
        // Configuration

        // Execution

        // Assertion
    }

}
