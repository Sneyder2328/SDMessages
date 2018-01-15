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

package com.sneyder.sdmessages.data.imageCompressor

import android.graphics.*
import android.os.Environment
import android.support.media.ExifInterface
import debug
import io.reactivex.Single
import io.reactivex.SingleEmitter
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

class AppImageCompressor @Inject constructor(): ImageCompressor() {

    override fun compressImage(
            filePath: String,
            maxWidth: Float,
            maxHeight: Float
    ): Single<File> {

        return Single.create({ emitter: SingleEmitter<File> ->

            var scaledBitmap: Bitmap? = null

            val options = BitmapFactory.Options()

            // by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
            // you try the use the bitmap here, you will get null.
            options.inJustDecodeBounds = true
            var bmp = BitmapFactory.decodeFile(filePath, options)

            // width and height values are set maintaining the aspect ratio of the image
            val (actualWidth, actualHeight) = getDimensionsMaintainingAspectRatio(options.outWidth.toFloat(), maxWidth, options.outHeight.toFloat(), maxHeight)

            // setting inSampleSize value allows to load a scaled down version of the original image
            options.inSampleSize = calculateInSampleSize(options, actualWidth.toInt(), actualHeight.toInt())

            // inJustDecodeBounds set to false to load the actual bitmap
            options.inJustDecodeBounds = false

            // this options allow android to claim the bitmap memory if it runs low on memory
            options.inPurgeable = true
            options.inInputShareable = true
            options.inTempStorage = ByteArray(16 * 1024)

            try {
                // load the bitmap from its path
                bmp = BitmapFactory.decodeFile(filePath, options)
            } catch (exception: OutOfMemoryError) {
                exception.printStackTrace()
            }
            try {
                scaledBitmap = Bitmap.createBitmap(actualWidth.toInt(), actualHeight.toInt(), Bitmap.Config.ARGB_8888)
            } catch (exception: OutOfMemoryError) {
                exception.printStackTrace()
            }

            val ratioX = actualWidth / options.outWidth.toFloat()
            val ratioY = actualHeight / options.outHeight.toFloat()
            val middleX = actualWidth / 2f
            val middleY = actualHeight / 2f

            val scaleMatrix = Matrix()
            scaleMatrix.setScale(ratioX, ratioY, middleX, middleY)

            val canvas = Canvas(scaledBitmap)
            canvas.matrix = scaleMatrix
            canvas.drawBitmap(bmp, middleX - bmp.width / 2, middleY - bmp.height / 2, Paint(Paint.FILTER_BITMAP_FLAG))

            // check the rotation of the image and display it properly
            val exif: ExifInterface
            try {
                exif = ExifInterface(filePath)

                val orientation = exif.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
                debug("Exif: " + orientation)
                val matrix = Matrix()
                when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> {
                        matrix.postRotate(90f)
                    }
                    ExifInterface.ORIENTATION_ROTATE_180 -> {
                        matrix.postRotate(180f)
                    }
                    ExifInterface.ORIENTATION_ROTATE_270 -> {
                        matrix.postRotate(270f)
                    }
                }
                debug("Exif: " + orientation)
                scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                        scaledBitmap!!.width, scaledBitmap.height, matrix,
                        true)
            } catch (e: IOException) {
                e.printStackTrace()
            }

            var fileOutputStream: FileOutputStream? = null
            val filename = getFilename()
            try {
                fileOutputStream = FileOutputStream(filename)
                // write the compressed bitmap at the destination specified by filename.
                scaledBitmap!!.compress(Bitmap.CompressFormat.JPEG, 80, fileOutputStream)
                emitter.onSuccess(File(filename))
            } catch (e: Exception) {
                emitter.onError(e)
                e.printStackTrace()
            } finally {
                try {
                    if (fileOutputStream != null) {
                        fileOutputStream.flush()
                        fileOutputStream.close()
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        })
                .doOnSuccess { debug("doOnSuccess compressImage($filePath, $maxWidth, $maxHeight) = $it") }
                .doOnError { error("doOnError compressImage($filePath, $maxWidth, $maxHeight) = ${it.message}") }
    }

    private fun getDimensionsMaintainingAspectRatio(actualWidth: Float, maxWidth: Float, actualHeight: Float, maxHeight: Float): Pair<Float, Float> {
        val imgRatio = (Math.min(actualWidth, actualHeight) / Math.max(actualWidth, actualHeight))
        var actualWidth1 = actualWidth
        var actualHeight1 = actualHeight
        if (actualWidth1 > maxWidth || actualHeight1 > maxHeight) {
            if (actualWidth1 > actualHeight1 && actualWidth1 > maxWidth) {
                actualWidth1 = maxWidth
                actualHeight1 = (imgRatio * actualWidth1)
            } else if (actualHeight1 > actualWidth1 && actualHeight1 > maxHeight) {
                actualHeight1 = maxHeight
                actualWidth1 = (imgRatio * actualHeight1)
            } else {
                actualWidth1 = maxWidth
                actualHeight1 = maxHeight
            }
        }
        return Pair(actualWidth1, actualHeight1)
    }

    private fun getFilename(): String {
        val file = File(Environment.getExternalStorageDirectory().path, "SDMessages/Images")
        if (!file.exists()) {
            file.mkdirs()
        }
        return file.absolutePath + "/" + System.currentTimeMillis() + ".jpg"
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val heightRatio = Math.round(height.toFloat() / reqHeight.toFloat())
            val widthRatio = Math.round(width.toFloat() / reqWidth.toFloat())
            inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
        }
        val totalPixels = (width * height).toFloat()
        val totalReqPixelsCap = (reqWidth * reqHeight * 2).toFloat()
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++
        }

        return inSampleSize
    }
}