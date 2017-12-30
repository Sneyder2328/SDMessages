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

package com.sneyder.sdmessages.data.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity(tableName = UserInfo.TABLE_NAME)
data class UserInfo(
        @SerializedName("userId") @Expose @PrimaryKey var userId: String = "",
        @SerializedName("displayName") @Expose var displayName: String = "",
        @SerializedName("birthDate") @Expose var birthDate: String = "",
        @SerializedName("photoUrl") @Expose var photoUrl: String? = "",
        @SerializedName("sessionId") @Expose var sessionId: String? = ""
) {
    companion object {
        const val TABLE_NAME = "UserInfo"
    }
}

data class UserRequest(
        val userId: String = "",
        val displayName: String = "",
        val email: String,
        var password: String = "",
        val typeLogin: String,
        val birthDate: String = "",
        val photoUrl: String = "",
        val accessToken: String = ""
)

enum class TypeLogin(val data: String){
    GOOGLE("Google"), FACEBOOK("Facebook"), EMAIL("Email")
}