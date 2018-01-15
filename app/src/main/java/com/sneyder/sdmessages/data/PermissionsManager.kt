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

package com.sneyder.sdmessages.data

import android.Manifest
import android.app.Activity
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.OnLifecycleEvent
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import debug
import isMarshmallowOrLater
import java.lang.ref.WeakReference

class PermissionsManager(activity: Activity, lifecycleOwner: LifecycleOwner) : LifecycleObserver {

    companion object {

        const val CAMERA = Manifest.permission.CAMERA

        const val WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE

        const val READ_EXTERNAL_STORAGE = "android.permission.READ_EXTERNAL_STORAGE"
    }

    private val activityWeakReference: WeakReference<Activity> = WeakReference(activity)

    init {
        lifecycleOwner.lifecycle.addObserver(this)
    }

    private var listFunctions: MutableMap<Int, () -> Unit> = HashMap()

    fun ifHasPermission(permissionToAskFor: String, requestCode: Int, func: () -> Unit) {
        if (isMarshmallowOrLater() && ContextCompat.checkSelfPermission(activityWeakReference.get()!!, permissionToAskFor) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activityWeakReference.get()!!, arrayOf(permissionToAskFor), requestCode)
            listFunctions.put(requestCode, func)
            return
        }
        func.invoke()
    }

    fun ifHasPermission(permissionsToAskFor: Array<String>, requestCode: Int, func: () -> Unit) {
        val pendingPermissions = permissionsToAskFor.filter {
            isMarshmallowOrLater() && ContextCompat.checkSelfPermission(activityWeakReference.get()!!, it) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()

        if(pendingPermissions.isNotEmpty()){
            ActivityCompat.requestPermissions(activityWeakReference.get()!!, pendingPermissions, requestCode)
            listFunctions.put(requestCode, func)
            return
        }
        func.invoke()
    }

    fun onRequestPermissionsResult(requestCode: Int, grantResults: IntArray) {
        if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
            listFunctions[requestCode]?.invoke()
            listFunctions.remove(requestCode)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy(){
        debug("onDestroy clear activity")
        activityWeakReference.clear()
    }
}
