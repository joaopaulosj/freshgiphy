package com.joao.freshgiphy.utils.extensions

import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

fun <T> Single<T>.rxSubscribe(
    onSuccess: ((t: T) -> Unit)? = null,
    onError: ((error: Throwable) -> Unit)? = null,
    subscribeOnScheduler: Scheduler? = Schedulers.io(),
    observeOnScheduler: Scheduler? = AndroidSchedulers.mainThread()
): DisposableSingleObserver<T> {
    return subscribeOn(subscribeOnScheduler)
        .observeOn(observeOnScheduler)
        .subscribeWith(object : DisposableSingleObserver<T>() {
            override fun onSuccess(t: T) {
                onSuccess?.let { it(t) }
            }

            override fun onError(e: Throwable) {
                onError?.let { it(e) }
            }
        })
}