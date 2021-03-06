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

@Entity(tableName = GroupInfo.TABLE_NAME)
data class GroupInfo(
        @SerializedName("groupId") @Expose @PrimaryKey var groupId: String = "",
        @SerializedName("name") @Expose var name: String = "",
        @SerializedName("adminId") @Expose var adminId: String = "",
        @SerializedName("typeAccess") @Expose var typeAccess: String = "",
        @SerializedName("password") @Expose var password: String = "",
        @SerializedName("pictureUrl") @Expose var pictureUrl: String= "") {

    companion object {
        const val TABLE_NAME = "GroupInfo"
    }

}