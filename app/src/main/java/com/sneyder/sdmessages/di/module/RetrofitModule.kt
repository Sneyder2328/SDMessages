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

package com.sneyder.sdmessages.di.module

import android.content.Context
import com.sneyder.sdmessages.data.remote.api.SDMessagesApi
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import com.google.gson.GsonBuilder
import com.google.gson.Gson
import com.sneyder.sdmessages.data.remote.api.ConnectivityInterceptor

@Module
class RetrofitModule {

    @Provides
    @Singleton
    fun provideConnectivityInterceptor(context: Context): ConnectivityInterceptor
            = ConnectivityInterceptor((context))

    @Provides
    @Singleton
    fun provideHttpClient(connectivityInterceptor: ConnectivityInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .addInterceptor(connectivityInterceptor)
                .build()
    }
    @Provides
    @Singleton
    fun gson(): Gson = GsonBuilder()
            .setLenient()
            .create()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit = Retrofit.Builder()
            .baseUrl(SDMessagesApi.END_POINT)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideSDMessagesApi(retrofit: Retrofit): SDMessagesApi = retrofit.create(SDMessagesApi::class.java)

}