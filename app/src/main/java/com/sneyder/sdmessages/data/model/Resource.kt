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

package com.sneyder.sdmessages.data.model

import android.support.annotation.StringRes
import com.sneyder.sdmessages.data.model.ResourceStatus.ERROR
import com.sneyder.sdmessages.data.model.ResourceStatus.LOADING
import com.sneyder.sdmessages.data.model.ResourceStatus.SUCCESS

/**
 * A generic class that holds a value with its loading resourceStatus.
 * @param <T>
</T> */
data class Resource<T>(val resourceStatus: ResourceStatus, val data: T?, @StringRes val message:  Int?) {

    companion object {

        fun <T> success(data: T): Resource<T> {
            return Resource(SUCCESS, data, null)
        }

        fun <T> error(@StringRes msg: Int? = null, data: T? = null): Resource<T> {
            return Resource(ERROR, data, msg)
        }

        fun <T> loading(data: T? = null): Resource<T> {
            return Resource(LOADING, data, null)
        }

    }

}