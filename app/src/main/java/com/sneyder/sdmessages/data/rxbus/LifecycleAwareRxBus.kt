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

import android.arch.lifecycle.*
import debug
import io.reactivex.disposables.CompositeDisposable

/*
class LifecycleAwareRxBus<T : Any> : RxBus<T>(), LifecycleObserver {

    private val compositeDisposable by lazy { CompositeDisposable() }

    private var onNextListener: (T) -> Unit = {}
    private var onErrorListener: (Throwable) -> Unit = {}
    private var onCompleteListener: () -> Unit = {}
    private var myLifecycleOwner: LifecycleOwner? = null

    fun subscribe(lifecycleOwner: LifecycleOwner,
                  onNext: (T) -> Unit = {},
                  onComplete: () -> Unit = {},
                  onError: (Throwable) -> Unit = {}
    ) {
        myLifecycleOwner = lifecycleOwner
        myLifecycleOwner!!.lifecycle.addObserver(this)
        onErrorListener = onError
        onCompleteListener = onComplete
        onNextListener = onNext
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun resume(){
        debug("RxBusWithLifecycleEvents resume")
        compositeDisposable.add(bus.subscribe(onNextListener, onErrorListener, onCompleteListener))
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun pause(){
        debug("RxBusWithLifecycleEvents pause")
        compositeDisposable.clear()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun destroy(){
        debug("RxBusWithLifecycleEvents destroy")
        compositeDisposable.dispose()
        myLifecycleOwner!!.lifecycle.removeObserver(this)
        myLifecycleOwner = null
    }
}*/