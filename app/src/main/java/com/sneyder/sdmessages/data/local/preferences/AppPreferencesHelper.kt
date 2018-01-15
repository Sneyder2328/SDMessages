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

package com.sneyder.sdmessages.data.local.preferences

import android.content.SharedPreferences
import debug
import edit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppPreferencesHelper @Inject constructor(private val sharedPreferences: SharedPreferences): PreferencesHelper {

    companion object {
        const val IS_LOGGED = "isLogged"
        const val CURRENT_USER_ID = "currentUserId"
        const val CURRENT_SESSION_ID = "currentSessionId"
    }

    @Synchronized override fun setLogged(isLogged: Boolean) {
        debug("setLogged = $isLogged")
        sharedPreferences.edit { putBoolean(IS_LOGGED, isLogged) }
    }

    @Synchronized override fun getLogged(): Boolean = sharedPreferences.getBoolean(IS_LOGGED, false)

    @Synchronized override fun setCurrentUserId(userId: String) {
        debug("setCurrentUserId = $userId")
        sharedPreferences.edit { putString(CURRENT_USER_ID, userId) }
    }

    @Synchronized override fun getCurrentUserId(): String {
        val value = sharedPreferences.getString(CURRENT_USER_ID, "")
        debug("getCurrentUserId = $value")
        return value
    }

    @Synchronized override fun setCurrentSessionId(sessionId: String) {
        debug("setCurrentSessionId = $sessionId")
        sharedPreferences.edit { putString(CURRENT_SESSION_ID, sessionId) }
    }

    @Synchronized override fun getCurrentSessionId(): String {
        val value = sharedPreferences.getString(CURRENT_SESSION_ID, "")
        debug("getCurrentSessionId = $value")
        return value
    }

    @Synchronized override fun clearPreferences() {
        sharedPreferences.edit { clear() }
    }
}