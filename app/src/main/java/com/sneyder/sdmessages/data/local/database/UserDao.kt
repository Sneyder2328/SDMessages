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

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.sneyder.sdmessages.data.model.UserInfo
import io.reactivex.Flowable

@Dao
abstract class UserDao : BaseDao<UserInfo> {

    @Query("SELECT * FROM ${UserInfo.TABLE_NAME} WHERE userId = :arg0")
    abstract fun findUserById(userId: String): Flowable<UserInfo>

    @Query("SELECT * FROM ${UserInfo.TABLE_NAME} WHERE typeUser == 'Friend'")
    abstract fun findFriends(): Flowable<List<UserInfo>>

    @Query("SELECT * FROM ${UserInfo.TABLE_NAME} WHERE displayName LIKE  '' || :arg0 || '%' AND typeUser != 'mySelf'")
    abstract fun findUsersByName(name: String): Flowable<List<UserInfo>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertUsers(vararg users: UserInfo)

    @Query("DELETE FROM ${UserInfo.TABLE_NAME}")
    abstract fun deleteTable()

}