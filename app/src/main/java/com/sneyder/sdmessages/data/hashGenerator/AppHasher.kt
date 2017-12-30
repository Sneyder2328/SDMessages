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

import android.util.Base64
import debug
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

class AppHasher : Hasher() {

    override external fun getPepper(): String

    companion object {
        // Used to load the 'native-lib' library on application startup.
        init {
            System.loadLibrary("native-lib")
        }
    }

    override fun hash(
            plainText: String,
            keyLength: Int, // 256-bits for AES-256, 128-bits for AES-128, etc
            iterationCount: Int
    ): String {
        debug("starting at: ${System.currentTimeMillis()}")
        val keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
        val pepper: ByteArray = Base64.decode("imfa${getPepper()}", Base64.DEFAULT)
        val keySpec = PBEKeySpec(plainText.toCharArray(), pepper, iterationCount, keyLength)
        val keyBytes = keyFactory.generateSecret(keySpec).encoded
        val hashed = Base64.encodeToString(SecretKeySpec(keyBytes, "AES").encoded, Base64.DEFAULT)
        debug("ending at: ${System.currentTimeMillis()} hashed = $hashed")
        return hashed.trim()
    }

}