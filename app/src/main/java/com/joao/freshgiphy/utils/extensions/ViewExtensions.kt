package com.joao.freshgiphy.utils.extensions

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import java.util.concurrent.TimeUnit

fun EditText.addTextWatcherDebounce(timeoutInMillis: Long, action: ((String) -> Unit)) {
    Observable.create(ObservableOnSubscribe<String> { subscriber ->
        addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // Do nothing
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Do nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                subscriber.onNext(s.toString())
            }
        })
    }).debounce(timeoutInMillis, TimeUnit.MILLISECONDS)
        .distinct()
        .observableSubscribe(onNext = { action(it) })

}
