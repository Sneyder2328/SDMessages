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
import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity(tableName = UserInfo.TABLE_NAME)
data class UserInfo(
        @SerializedName("userId") @Expose @PrimaryKey var userId: String = "",
        @SerializedName("displayName") @Expose var displayName: String = "",
        @SerializedName("birthDate") @Expose var birthDate: String = "",
        @SerializedName("photoUrl") @Expose var photoUrl: String? = "",
        @SerializedName("sessionId") @Expose var sessionId: String? = "",
        @SerializedName("typeUser") @Expose var typeUser: String? = "",
        @SerializedName("firebaseTokenId") @Expose var firebaseTokenId: String? = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(userId)
        parcel.writeString(displayName)
        parcel.writeString(birthDate)
        parcel.writeString(photoUrl)
        parcel.writeString(sessionId)
        parcel.writeString(typeUser)
        parcel.writeString(firebaseTokenId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserInfo> {
        const val TABLE_NAME = "UserInfo"
        override fun createFromParcel(parcel: Parcel): UserInfo {
            return UserInfo(parcel)
        }

        override fun newArray(size: Int): Array<UserInfo?> {
            return arrayOfNulls(size)
        }
    }
}