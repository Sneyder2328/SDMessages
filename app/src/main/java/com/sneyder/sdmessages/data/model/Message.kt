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

@Entity(tableName = Message.TABLE_NAME)
data class Message(
        @SerializedName("messageId") @Expose var messageId: Int = 0,
        @SerializedName("content") @Expose var content: String = "",
        @SerializedName("senderId") @Expose var senderId: String = "",
        @SerializedName("recipientId") @Expose var recipientId: String = "",
        @SerializedName("typeContent") @Expose var typeContent: String = "",
        /**
         * dateCreate act as the PrimaryKey here in the local DB because for the way if works there could easily be a messageId(auto-incremented)
         */
        @SerializedName("dateCreated") @Expose @PrimaryKey var dateCreated: Long = 0,
        @SerializedName("dateExpiry") @Expose var dateExpiry: Long = 0,
        var received: Boolean = false,
        var viewed: Boolean = false
) {

    companion object {
        const val TABLE_NAME = "Message"
    }

/*
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as Message
        return messageId == that.messageId &&
                content == that.content &&
                typeContent == that.typeContent &&
                recipientId == that.recipientId &&
                senderId == that.senderId
    }

    override fun hashCode(): Int {
        return dateCreated.toInt()
    }*/
}