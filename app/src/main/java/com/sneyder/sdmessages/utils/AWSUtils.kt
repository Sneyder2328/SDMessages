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

package com.sneyder.sdmessages.utils

import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest
import debug
import generateImageFileName

const val DEFAULT_BUCKET_NAME = "sdmessages-userfiles-mobilehub-100489725"
const val BUCKET_KEY_SEPARATOR = "---"

/**
 * Returns a temporal S3 url with the key if the url contains the [DEFAULT_BUCKET_NAME]
 * Otherwise, returns the same url without modifications
 * @see [getS3BucketWithKey]
 */
fun generateS3UrlIfApplicable(url: String): String {
    debug("generateS3UrlIfApplicable url = $url")
    val newUrl = if(url.contains(DEFAULT_BUCKET_NAME)){
        generatePresignedS3Url(url.substring(url.indexOf(BUCKET_KEY_SEPARATOR) + BUCKET_KEY_SEPARATOR.length))
    } else url
    debug("generateS3UrlIfApplicable newUrl = $newUrl")
    return newUrl
}

/**
 * Returns a temporal S3 url with the key passed on
 */
fun generatePresignedS3Url(key: String): String {
    debug("generatePresignedS3Url key = $key")
    val generatePresignedUrlRequest = GeneratePresignedUrlRequest(DEFAULT_BUCKET_NAME, key)
    return AmazonS3Client(AWSMobileClient.getInstance().credentialsProvider).generatePresignedUrl(generatePresignedUrlRequest).toString().also { debug("generatePresignedS3Url url = $it") }
}

/**
 * Returns a 'url' based on bucketName and key
 * It should be used to get the base urls to save in the database
 * @see [generateS3UrlIfApplicable]
 */
fun getS3BucketWithKey(key: String, bucketName: String = DEFAULT_BUCKET_NAME) = "$bucketName$BUCKET_KEY_SEPARATOR$key"

fun generateKeyForS3() = "public/${generateImageFileName()}"