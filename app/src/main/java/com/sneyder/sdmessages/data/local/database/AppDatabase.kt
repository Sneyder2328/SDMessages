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

package com.sneyder.sdmessages.data.local.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.sneyder.sdmessages.data.model.GroupInfo
import com.sneyder.sdmessages.data.model.Message
import com.sneyder.sdmessages.data.model.UserInfo

@Database(entities = [(UserInfo::class), (GroupInfo::class), (Message::class)], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

    abstract fun groupDao(): GroupDao

    abstract fun messageDao(): MessageDao

    fun clearDatabase(){
        userDao().deleteTable()
        groupDao().deleteTable()
        messageDao().deleteTable()
    }
}