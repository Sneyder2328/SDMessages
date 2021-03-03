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

package com.sneyder.sdmessages.utils.dialogs

import android.app.DatePickerDialog
import android.app.Dialog
import android.support.v4.app.DialogFragment
import android.os.Bundle
import java.util.*

/**
 * Subclass of [DialogFragment] used to show a [DatePickerDialog]
 * IMPORTANT: All activities that use this dialog must implement the interface [android.app.DatePickerDialog.OnDateSetListener]
 */
class DatePickerDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val calendar = Calendar.getInstance()
        return android.app.DatePickerDialog(
                activity,
                getRegisteredListener(),
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    private fun getRegisteredListener(): DatePickerDialog.OnDateSetListener? {
        return when {
            parentFragment is DatePickerDialog.OnDateSetListener -> parentFragment as DatePickerDialog.OnDateSetListener
            context is DatePickerDialog.OnDateSetListener -> context as DatePickerDialog.OnDateSetListener
            activity is DatePickerDialog.OnDateSetListener -> activity as DatePickerDialog.OnDateSetListener
            else -> throw NoOnDateSetListenerException()
        }
    }
}
