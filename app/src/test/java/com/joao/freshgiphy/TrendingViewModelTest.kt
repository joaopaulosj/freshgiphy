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
import com.joao.freshgiphy.repositories.GiphyRepository
import com.joao.freshgiphy.ui.datasource.TrendingDataFactory
import com.joao.freshgiphy.utils.extensions.rxSubscribe
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
class TrendingViewModelTest : LifecycleOwner {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Mock
    private lateinit var repository: GiphyRepository

    @Mock
    private lateinit var listStatusObserver: Observer<ListStatus>

    private val lifecycle by lazy { LifecycleRegistry(this) }

    private lateinit var viewModel: TrendingViewModel
    private lateinit var testScheduler: TestScheduler
    private lateinit var apiSuccessResponseMock: ApiResponse
    private lateinit var apiEmptyResponseMock: ApiResponse
    private lateinit var apiCodeErrorResponseMock: ApiResponse

    @Before
    fun setup() {
        initMocks()
        MockitoAnnotations.initMocks(this)

        testScheduler = TestScheduler()

        viewModel = spy(TrendingViewModel(repository))
        viewModel.trendingDataFactory = TrendingDataFactory(viewModel)
    }

    private fun initMocks() {
        val gifImage = GifImage(downsized = GifPreview("url", 0, 0))
        val gifResponse = GifResponse(id = "id", url = "url", images = gifImage)
        val data = listOf(gifResponse)
        val pagination = PaginationResponse(1, 1, 0)
        val meta = MetaResponse(200, "")
        apiSuccessResponseMock = ApiResponse(data, pagination, meta)
        apiEmptyResponseMock = ApiResponse(emptyList(), pagination, meta)
        apiCodeErrorResponseMock = ApiResponse(emptyList(), pagination, MetaResponse(401, "Unauthorized"))
    }

    override fun getLifecycle(): Lifecycle {
        return lifecycle
    }

    @Test
    fun `WHEN do first gifs request and receives data THEN should update status to default`() {
        // Configuration
        whenever(repository.getTrending(0)).thenReturn(Single.just(apiSuccessResponseMock))
        viewModel.listStatusEvent().observeForever(listStatusObserver)

        // Execution
        viewModel.getTrending(0).rxSubscribe(
            subscribeOnScheduler = testScheduler,
            observeOnScheduler = testScheduler
        )

        testScheduler.triggerActions()

        // Assertion
        verify(listStatusObserver).onChanged(ListStatus(Status.LOADING))
        verify(listStatusObserver).onChanged(ListStatus(Status.DEFAULT))
    }

    @Test
    fun `WHEN do first gifs request and receives no data THEN should update status to empty`() {
        // Configuration
        whenever(repository.getTrending(0)).thenReturn(Single.just(apiEmptyResponseMock))
        viewModel.listStatusEvent().observeForever(listStatusObserver)

        // Execution
        viewModel.getTrending(0).rxSubscribe(
            subscribeOnScheduler = testScheduler,
            observeOnScheduler = testScheduler
        )

        testScheduler.triggerActions()

        // Assertion
        verify(listStatusObserver).onChanged(ListStatus(Status.LOADING))
        verify(listStatusObserver).onChanged(ListStatus(Status.EMPTY))
    }

    @Test
    fun `WHEN do first gifs request and receives an api code error THEN should show error`() {
        // Configuration
        whenever(repository.getTrending(0)).thenReturn(Single.just(apiCodeErrorResponseMock))
        viewModel.listStatusEvent().observeForever(listStatusObserver)

        // Execution
        viewModel.getTrending(0).rxSubscribe(
            subscribeOnScheduler = testScheduler,
            observeOnScheduler = testScheduler
        )

        testScheduler.triggerActions()

        // Assertion
        verify(listStatusObserver).onChanged(ListStatus(Status.LOADING))
        verify(listStatusObserver).onChanged(ListStatus(Status.ERROR, "Unauthorized"))
    }

    @Test
    fun `WHEN do first gifs request and receives an request error THEN should show error`() {
        // Configuration
        whenever(repository.getTrending(0)).thenReturn(Single.error(Throwable("Error")))
        viewModel.listStatusEvent().observeForever(listStatusObserver)

        // Execution
        viewModel.getTrending(0).rxSubscribe(
            subscribeOnScheduler = testScheduler,
            observeOnScheduler = testScheduler
        )

        testScheduler.triggerActions()

        // Assertion
        verify(listStatusObserver).onChanged(ListStatus(Status.LOADING))
        verify(listStatusObserver).onChanged(ListStatus(Status.ERROR, "Error"))
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
    fun `WHEN refresh is called THEN should invalidate paging data factory`() {
        // Execution
        viewModel.refresh()

        // Assertion
        verify(viewModel, times(1)).invalidateDataFactory()
    }

    @Test
    fun `WHEN a search is entered THEN should do search request with text`() {
        // Execution
        viewModel.search("search")

        // Assertion
        verify(viewModel, times(1)).updateQueryOnDataFactory("search")
        verify(viewModel, times(1)).invalidateDataFactory()
    }

    @Test
    fun `WHEN a search request receives data THEN should update status to default`() {
        // Configuration
        whenever(repository.search("query", 0)).thenReturn(Single.just(apiSuccessResponseMock))
        viewModel.listStatusEvent().observeForever(listStatusObserver)

        // Execution
        viewModel.search("query", 0).rxSubscribe(
            subscribeOnScheduler = testScheduler,
            observeOnScheduler = testScheduler
        )

        testScheduler.triggerActions()

        // Assertion
        verify(listStatusObserver).onChanged(ListStatus(Status.LOADING))
        verify(listStatusObserver).onChanged(ListStatus(Status.DEFAULT))
    }

    @Test
    fun `WHEN a search request doesnt receive data THEN should update status to empty`() {
        // Configuration
        whenever(repository.search("query", 0)).thenReturn(Single.just(apiEmptyResponseMock))
        viewModel.listStatusEvent().observeForever(listStatusObserver)

        // Execution
        viewModel.search("query", 0).rxSubscribe(
            subscribeOnScheduler = testScheduler,
            observeOnScheduler = testScheduler
        )

        testScheduler.triggerActions()

        // Assertion
        verify(listStatusObserver).onChanged(ListStatus(Status.LOADING))
        verify(listStatusObserver).onChanged(ListStatus(Status.EMPTY))
    }

    @Test
    fun `WHEN a search request receives api code error THEN should update status to error`() {
        // Configuration
        whenever(repository.search("query", 0)).thenReturn(Single.just(apiCodeErrorResponseMock))
        viewModel.listStatusEvent().observeForever(listStatusObserver)

        // Execution
        viewModel.search("query", 0).rxSubscribe(
            subscribeOnScheduler = testScheduler,
            observeOnScheduler = testScheduler
        )

        testScheduler.triggerActions()

        // Assertion
        verify(listStatusObserver).onChanged(ListStatus(Status.LOADING))
        verify(listStatusObserver).onChanged(ListStatus(Status.ERROR, "Unauthorized"))
    }

    @Test
    fun `WHEN a search request receives request error THEN should update status to error`() {
        // Configuration
        whenever(repository.search("query", 0)).thenReturn(Single.error(Throwable("error")))
        viewModel.listStatusEvent().observeForever(listStatusObserver)

        // Execution
        viewModel.search("query", 0).rxSubscribe(
            subscribeOnScheduler = testScheduler,
            observeOnScheduler = testScheduler
        )

        testScheduler.triggerActions()

        // Assertion
        verify(listStatusObserver).onChanged(ListStatus(Status.LOADING))
        verify(listStatusObserver).onChanged(ListStatus(Status.ERROR, "error"))
    }

}
