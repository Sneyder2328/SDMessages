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

import com.sneyder.sdmessages.data.model.Resource
import com.sneyder.sdmessages.data.model.ResourceStatus

inline fun <T> Resource<T>?.ifSuccess(func: (T) -> Unit){
    if (this == null) return
    if (resourceStatus == ResourceStatus.SUCCESS){
        func(data!!)
    }
}

inline fun <T> Resource<T>?.ifError(func: (Int?) -> Unit){
    if (this == null) return
    if (resourceStatus == ResourceStatus.ERROR){
        func(message)
    }
}

inline fun <T> Resource<T>?.ifLoading(func: () -> Unit){
    if (this == null) return
    if (resourceStatus == ResourceStatus.LOADING){
        func()
    }
}