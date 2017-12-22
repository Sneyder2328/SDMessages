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

package com.sneyder.sdmessages.ui.base

import android.arch.lifecycle.ViewModel
import com.sneyder.rememberconcepts.utils.schedulers.SchedulerProvider
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class BaseViewModel(val schedulersProvider: SchedulerProvider): ViewModel() {

    private val compositeDisposable by lazy { CompositeDisposable() }

    fun <T>Flowable<T>.applySchedulers(): Flowable<T> = this
            .subscribeOn(schedulersProvider.io())
            .observeOn(schedulersProvider.ui())

    fun <T> Single<T>.applySchedulers(): Single<T> = this
            .subscribeOn(schedulersProvider.io())
            .observeOn(schedulersProvider.ui())

    fun Completable.applySchedulers(): Completable = this
            .subscribeOn(schedulersProvider.io())
            .observeOn(schedulersProvider.ui())

    /**
     * Adds a disposable to compositeDisposable
     */
    fun add(disposable: Disposable): Boolean {
        return compositeDisposable.add(disposable)
    }

    override fun onCleared() {
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.dispose()
        }
        super.onCleared()
    }
}