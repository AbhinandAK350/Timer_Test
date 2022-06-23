package com.ctrlroom.myapplication.utils

import android.content.Context
import android.content.Context.BATTERY_SERVICE
import android.os.BatteryManager
import android.util.Log
import java.io.FileWriter
import java.io.IOException

object Util {

    fun getBatteryLevel(context: Context): Int {
        val bm = context.getSystemService(BATTERY_SERVICE) as BatteryManager?
        return bm!!.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
    }

    fun writeToJson(context: Context, mJsonResponse: String?) {
        try {
            val file = FileWriter(context.filesDir.path + "/" + "log.json", true)
            file.append(mJsonResponse)
            file.flush()
            file.close()
        } catch (e: IOException) {
            Log.e("TAG", "Error in Writing: " + e.localizedMessage)
        }
    }

}