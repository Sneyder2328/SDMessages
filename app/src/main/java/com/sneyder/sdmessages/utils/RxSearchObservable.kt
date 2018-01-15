/*
 * Copyright (C) 2018 Sneyder Angulo.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sneyder.sdmessages.utils

import android.support.v7.widget.SearchView
import android.widget.EditText
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.subjects.PublishSubject
import addTextChangedListener

object RxSearchObservable {

    fun fromSearchView(searchView: SearchView): Flowable<String> {

        val subject = PublishSubject.create<String>()

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(s: String): Boolean {
                subject.onComplete()
                return true
            }
            override fun onQueryTextChange(text: String): Boolean {
                subject.onNext(text)
                return true
            }
        })
        return subject.toFlowable(BackpressureStrategy.LATEST)
    }

    fun fromEditText(editText: EditText): Flowable<String> {
        val subject = PublishSubject.create<String>()
        editText.addTextChangedListener({
            subject.onNext(it.toString())
        })
        return subject.toFlowable(BackpressureStrategy.LATEST)
    }
}