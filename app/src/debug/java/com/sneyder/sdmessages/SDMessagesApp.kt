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

package com.sneyder.sdmessages

import android.os.StrictMode
import timber.log.Timber

class SDMessagesApp: BaseApp() {

    override fun onCreate() {
        super.onCreate()
        initTimber()
        setStrictMode()
    }

    private fun initTimber() {
        Timber.plant(MyCustomDebugTree())
    }

    class MyCustomDebugTree: Timber.DebugTree() {
        override fun createStackElementTag(element: StackTraceElement): String? {
            return "${super.createStackElementTag(element)}:${element.lineNumber}"
        }
    }

    private fun setStrictMode() {
        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .penaltyDeath()
                .build())
        StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .penaltyDeath()
                .build())
    }

}