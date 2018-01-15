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


import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.Observer


fun <T> LiveData<T>.observeJustOnce(owner: LifecycleOwner, observer: Observer<T>) {
    observe(owner, Observer {  item->
        observer.onChanged(item)
        removeObservers(owner)
    })
}
fun <T> LiveData<T>.distinc(): LiveData<T> {
    val distinctLiveData = MediatorLiveData<T>()
    distinctLiveData.addSource(this, object : Observer<T>{
        private var initialized = false
        private var lastObj: T? = null
        override fun onChanged(obj: T?) {
            if(!initialized){
                initialized = true
                lastObj = obj
                distinctLiveData.postValue(lastObj)
            } else{
                if((obj == null && lastObj != null) || obj != lastObj){
                    lastObj = obj
                    distinctLiveData.postValue(lastObj)
                }
            }
        }
    })
    return distinctLiveData
}


/**
 * Utility function intended to subscribe LiveData objects from Fragments
 * It's important to avoid certain memory leaks due to Fragments' lifecycle
 * More info about this issue in the link below at section 3. Resetting an existing observer
 * https://medium.com/@BladeCoder/architecture-components-pitfalls-part-1-9300dd969808
 */
fun <T> LiveData<T>.reObserve(owner: LifecycleOwner, observer: Observer<T>) {
    removeObserver(observer)
    observe(owner, observer)
}