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

package com.sneyder.sdmessages.di.builder

import com.sneyder.sdmessages.ui.home.HomeActivity
import com.sneyder.sdmessages.ui.login.LogInActivity
import com.sneyder.sdmessages.ui.main.MainActivity
import com.sneyder.sdmessages.ui.main.chats.ChatsFragmentProvider
import com.sneyder.sdmessages.ui.register.RegisterActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuilder {

    @ContributesAndroidInjector()
    abstract fun bindHomeActivity(): HomeActivity

    @ContributesAndroidInjector()
    abstract fun bindRegisterActivity(): RegisterActivity

    @ContributesAndroidInjector()
    abstract fun bindLogInActivity(): LogInActivity

    @ContributesAndroidInjector(modules = [(ChatsFragmentProvider::class)])
    abstract fun bindMainActivity(): MainActivity
}