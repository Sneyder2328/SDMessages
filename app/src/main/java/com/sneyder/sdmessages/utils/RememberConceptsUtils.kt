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

package com.sneyder.rememberconcepts.utils

import org.json.JSONArray
import org.json.JSONObject
import java.util.ArrayList
import java.util.concurrent.TimeUnit

/**
 * Returns the due date for the value received(learningStage)
 */
fun Int.calculateDueDate() = when (this) {
    0 -> -1
    1 -> TimeUnit.MINUTES.toMillis(10)
    2 -> TimeUnit.HOURS.toMillis(6)
    3 -> TimeUnit.DAYS.toMillis(1)
    4 -> TimeUnit.DAYS.toMillis(3)
    5 -> TimeUnit.DAYS.toMillis(7)
    6 -> TimeUnit.DAYS.toMillis(21)
    7 -> TimeUnit.DAYS.toMillis(60)
    8 -> TimeUnit.DAYS.toMillis(100)
    else -> (this - 6) * TimeUnit.DAYS.toMillis(100)
} + System.currentTimeMillis()


fun List<Pair<String, String>>.fromContentToJson(): String {
    val jsonArray = JSONArray()
    forEach {
        val jsonObject = JSONObject()
        jsonObject.put(it.first, it.second)
        jsonArray.put(jsonObject)
    }
    return jsonArray.toString()
}

fun String.fromJsonToContent(): List<Pair<String, String>> {
    val content: MutableList<Pair<String, String>> = ArrayList()
    val jsonArray = JSONArray(this)

    (0 until jsonArray.length())
            .map { jsonArray.getJSONObject(it) }
            .forEach { jsonObject ->
                jsonObject.keys().forEach {
                    content.add(it to jsonObject.getString(it))
                }
            }
    return content
}
