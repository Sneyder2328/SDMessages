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

package com.sneyder.sdmessages.di.component

import android.app.Application
import com.sneyder.sdmessages.BaseApp
import com.sneyder.sdmessages.data.service.AppFirebaseMessagingService
import com.sneyder.sdmessages.data.service.NotificationBroadcastReceiver
import com.sneyder.sdmessages.data.service.NotificationIntentService
import com.sneyder.sdmessages.di.builder.ActivityBuilder
import com.sneyder.sdmessages.di.module.AppModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [(AndroidSupportInjectionModule::class), (AppModule::class), (ActivityBuilder::class)])
interface AppComponent {

    @Component.Builder
    interface Builder{

        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent

    }

    fun inject(app: BaseApp)

    fun inject(firebaseMessagingService: AppFirebaseMessagingService)

    fun inject(notificationIntentService: NotificationIntentService)

    fun inject(notificationBroadcastReceiver: NotificationBroadcastReceiver)

}