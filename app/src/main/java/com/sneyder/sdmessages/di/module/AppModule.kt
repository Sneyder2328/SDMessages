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

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.sneyder.sdmessages.utils.schedulers.AppSchedulerProvider
import com.sneyder.sdmessages.utils.schedulers.SchedulerProvider
import com.sneyder.sdmessages.data.aws.AppS3FilesManager
import com.sneyder.sdmessages.data.aws.S3FilesManager
import com.sneyder.sdmessages.data.hashGenerator.AppHasher
import com.sneyder.sdmessages.data.hashGenerator.Hasher
import com.sneyder.sdmessages.data.imageCompressor.AppImageCompressor
import com.sneyder.sdmessages.data.imageCompressor.ImageCompressor
import com.sneyder.sdmessages.data.local.preferences.AppPreferencesHelper
import com.sneyder.sdmessages.data.local.preferences.PreferencesHelper
import com.sneyder.sdmessages.data.model.FriendRequest
import com.sneyder.sdmessages.data.rxbus.OneShotLiveDataBus
import com.sneyder.sdmessages.data.service.AppFirebaseMessagingService
import dagger.Module
import dagger.Provides
import defaultSharedPreferences
import javax.inject.Singleton

@Module(includes = [(ViewModelModule::class), (RepositoriesModule::class), (RoomDatabaseModule::class), (RetrofitModule::class)])
class AppModule {

    @Provides
    @Singleton
    fun provideContext(application: Application): Context = application

    @Provides
    @Singleton
    fun provideSharedPreferences(context: Context): SharedPreferences = context.defaultSharedPreferences

    @Provides
    @Singleton
    fun providePreferencesHelper(appPreferencesHelper: AppPreferencesHelper): PreferencesHelper = appPreferencesHelper

    @Provides
    @Singleton
    fun provideSchedulerProvider(): SchedulerProvider = AppSchedulerProvider()

    @Provides
    @Singleton
    fun provideHasher(): Hasher = AppHasher()

    @Provides
    @Singleton
    fun provideS3FilesManager(appS3FilesManager: AppS3FilesManager): S3FilesManager = appS3FilesManager

    @Provides
    fun provideImageCompressor(appImageCompressor: AppImageCompressor): ImageCompressor = appImageCompressor

    @Provides
    @Singleton
    fun provideFriendRequestsBus(): OneShotLiveDataBus<FriendRequest> = OneShotLiveDataBus()

    @Provides
    @Singleton
    fun provideNewMessagesBus(): OneShotLiveDataBus<AppFirebaseMessagingService.NewMessageData> = OneShotLiveDataBus()

}