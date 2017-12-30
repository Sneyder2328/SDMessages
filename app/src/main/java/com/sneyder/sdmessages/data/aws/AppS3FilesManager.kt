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

import android.content.Context
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferType
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.services.s3.AmazonS3Client
import debug
import error
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.FlowableEmitter
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppS3FilesManager
@Inject constructor(private val applicationContext: Context) : S3FilesManager() {

    private val transferUtility by lazy {
        TransferUtility.builder()
                .context(applicationContext)
                .awsConfiguration(AWSMobileClient.getInstance().configuration)
                .s3Client(AmazonS3Client(AWSMobileClient.getInstance().credentialsProvider))
                .build()
    }

    override fun uploadFile(file: File, key: String): Flowable<Int> {
        return Flowable.create({ emitter: FlowableEmitter<Int> ->
            val uploadObserver = transferUtility.upload(key, file)

            uploadObserver.setTransferListener(object : TransferListener {

                override fun onStateChanged(id: Int, state: TransferState) {
                    debug("onStateChanged state = $state")
                    if (TransferState.COMPLETED === state) {
                        emitter.onComplete()
                    }
                }

                override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
                    val percentDone = (bytesCurrent.toFloat() / bytesTotal.toFloat() * 100).toInt()
                    debug("onProgressChanged ID:$id   bytesCurrent: $bytesCurrent   bytesTotal: $bytesTotal $percentDone%")
                    emitter.onNext(percentDone)
                }

                override fun onError(id: Int, ex: Exception) {
                    error("onError id = $id ex = ${ex.message}")
                    emitter.onError(ex)
                }

            })

        }, BackpressureStrategy.LATEST)
                .doOnNext { debug("doOnNext uploadFile($file, $key) = $it") }
                .doOnComplete { debug("doOnComplete uploadFile($file, $key)") }
                .doOnError { error("doOnError uploadFile($file, $key) = ${it.message}") }
    }

    override fun cancelAll() {
        transferUtility.cancelAllWithType(TransferType.ANY)
    }
}