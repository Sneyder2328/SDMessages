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

package com.sneyder.sdmessages.ui.main.profile

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Build
import android.renderscript.RSRuntimeException
import com.sneyder.sdmessages.utils.FastBlur
import com.sneyder.sdmessages.utils.RSBlur
import com.squareup.picasso.Transformation

class BlurTransformation(private val context: Context, private val radius: Int = 10, private val sampling: Int = 1): Transformation {
    override fun transform(source: Bitmap): Bitmap {
        val scaledWidth = source.width / sampling
        val scaledHeight = source.height / sampling

        var bitmap = Bitmap.createBitmap(scaledWidth, scaledHeight, Bitmap.Config.ARGB_8888)

        val canvas = Canvas(bitmap)
        canvas.scale(1 / sampling.toFloat(), 1 / sampling.toFloat())
        val paint = Paint()
        paint.flags = Paint.FILTER_BITMAP_FLAG
        canvas.drawBitmap(source, 0f, 0f, paint)

        bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            try {
                RSBlur.blur(context, bitmap, radius)
            } catch (e: RSRuntimeException) {
                FastBlur.blur(bitmap, radius, true)
            }

        } else {
            FastBlur.blur(bitmap, radius, true)
        }

        source.recycle()

        return bitmap
    }

    override fun key(): String {
        return "BlurTransformation(radius=$radius, sampling=$sampling)"
    }
}