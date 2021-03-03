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

import android.os.Parcel
import android.os.Parcelable

data class FriendRequest(
        val fromUserId: String,
        val fromUserName: String,
        val fromPhotoUrl: String,
        val toUserId: String,
        val message: String) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(fromUserId)
        parcel.writeString(fromUserName)
        parcel.writeString(fromPhotoUrl)
        parcel.writeString(toUserId)
        parcel.writeString(message)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FriendRequest> {
        override fun createFromParcel(parcel: Parcel): FriendRequest {
            return FriendRequest(parcel)
        }

        override fun newArray(size: Int): Array<FriendRequest?> {
            return arrayOfNulls(size)
        }
    }
}