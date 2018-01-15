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

enum class TypeUser(val data: String){
    /**
     * It means that the user in question is me
     */
    MYSELF("mySelf"),
    /**
     * Regular and normal user, it's not neither my friend nor has anything in common with me
     */
    UNKNOWN("Unknown"),
    /**
     * Has a friend relationship with me, we can send messages to each other
     */
    FRIEND("Friend"),
    /**
     * There's a blocked relationship between us, we cannot communicate nor see our personal info[blocked users don't have any of these: displayName, birthDate, photoUrl, lastConnection...]
     */
    BLOCKED("Blocked"),
    /**
     * There's a pending friend request between us, we still cannot chat
     */
    PENDING_REQUEST("PendingRequest")
}