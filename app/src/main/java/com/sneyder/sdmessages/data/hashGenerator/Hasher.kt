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

package com.sneyder.sdmessages.data.hashGenerator

import kotlinx.coroutines.experimental.Deferred

abstract class Hasher {

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * It returns the pepper that will be used for hashing.
     */
    abstract fun getPepper(): String

    /**
     * Use this to derive the key from the password:
     * */
    abstract fun hash(
            plainText: String,
            keyLength: Int = 256, // 256-bits for AES-256, 128-bits for AES-128, etc
            iterationCount: Int = 1000
    ): Deferred<String>

}