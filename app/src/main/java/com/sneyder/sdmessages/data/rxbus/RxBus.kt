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

package com.sneyder.sdmessages.data.rxbus

import debug
import io.reactivex.BackpressureStrategy
import io.reactivex.Observable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.subjects.PublishSubject

class RxBus {

    init {
        debug("creating a new instance of RxBus")
    }

    companion object {
        const val DEFAULT_SUBJECT = "defaultSubject"
    }

    //private val bus: PublishSubject<T> by lazy { PublishSubject.create<Any>() }
    private val subjectMap by lazy { HashMap<String, PublishSubject<Any>>() }

    fun publish(subject: String = DEFAULT_SUBJECT, message: Any): Boolean {
        val hasObservers = getPublishSubject(subject).hasObservers()
        debug("Rxbus publish($message) hasObservers = $hasObservers")
        getPublishSubject(subject).onNext(message)
        return hasObservers
    }

    fun subscribe(subject: String = DEFAULT_SUBJECT,
                  onError: (Throwable) -> Unit = {},
                  onComplete: () -> Unit = {},
                  onNext: (Any) -> Unit = {}) {
        getPublishSubject(subject).subscribe(onNext, onError, onComplete)
    }

    fun unsubscribe(subject: String = DEFAULT_SUBJECT) {
        getPublishSubject(subject).onComplete()
        subjectMap.remove(subject)
    }

    private fun getPublishSubject(subject: String) = subjectMap.getOrPut(subject, { PublishSubject.create<Any>() })

}