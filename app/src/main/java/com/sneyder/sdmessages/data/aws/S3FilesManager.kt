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

package com.sneyder.sdmessages.data.aws

import io.reactivex.Flowable
import java.io.File

abstract class S3FilesManager {

    abstract fun uploadFileCompressed(
            file: File,
            key: String,
            maxWidth: Float = 1040f,
            maxHeight: Float = 1040f): Flowable<Int>

    abstract fun cancelAll()

}