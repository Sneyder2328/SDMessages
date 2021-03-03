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

import android.arch.persistence.room.*
import com.sneyder.sdmessages.data.model.Message
import io.reactivex.Flowable

@Dao
abstract class MessageDao : BaseDao<Message> {

    @Query("SELECT * FROM ${Message.TABLE_NAME} WHERE (senderId = :userId AND recipientId = :friendUserId) OR (senderId = :friendUserId AND recipientId = :userId) ORDER BY dateCreated")
    abstract fun findMessagesWithUserId(userId: String, friendUserId: String): Flowable<List<Message>>

    @Query("SELECT * FROM ${Message.TABLE_NAME}")
    abstract fun findMessages(): Flowable<List<Message>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertMessages(vararg messages: Message)

    @Query("DELETE FROM ${Message.TABLE_NAME}")
    abstract fun deleteTable()

}