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

package com.sneyder.sdmessages.data.remote.api

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import debug
import isNetworkConnected
import com.sneyder.sdmessages.utils.NoConnectivityException


class ConnectivityInterceptor(private val context: Context) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val isNetworkActive = context.isNetworkConnected()
        debug("intercept isNetworkActive = $isNetworkActive")
        return if (!isNetworkActive) {
            throw NoConnectivityException()
        } else {
            chain.proceed(chain.request())
        }
    }
}