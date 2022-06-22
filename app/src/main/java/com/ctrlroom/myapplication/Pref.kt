package com.ctrlroom.myapplication

import android.annotation.SuppressLint
import android.content.Context

object Pref {
    @JvmStatic
    fun putString(context: Context, key: String?, value: String?) {
        val sharedPreferences =
            context.getSharedPreferences("testcd", Context.MODE_PRIVATE)
        @SuppressLint("CommitPrefEdits") val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    @JvmStatic
    fun putInt(context: Context, key: String?, value: Int) {
        val sharedPreferences =
            context.getSharedPreferences("testcd", Context.MODE_PRIVATE)
        @SuppressLint("CommitPrefEdits") val editor = sharedPreferences.edit()
        editor.putInt(key, value)
        editor.apply()
    }

    @JvmStatic
    fun putBoolean(context: Context, key: String?, value: Boolean?) {
        val sharedPreferences =
            context.getSharedPreferences("testcd", Context.MODE_PRIVATE)
        @SuppressLint("CommitPrefEdits") val editor = sharedPreferences.edit()
        editor.putBoolean(key, value!!)
        editor.apply()
    }

    @JvmStatic
    fun getString(context: Context, key: String?, defaultValue: String?): String? {
        val sharedPreferences =
            context.getSharedPreferences("testcd", Context.MODE_PRIVATE)
        return sharedPreferences.getString(key, defaultValue)
    }

    @JvmStatic
    fun getInt(context: Context, key: String?, defaultValue: Int): Int {
        val sharedPreferences =
            context.getSharedPreferences("testcd", Context.MODE_PRIVATE)
        return sharedPreferences.getInt(key, defaultValue)
    }

    @JvmStatic
    fun getBoolean(context: Context, key: String?, defaultValue: Boolean): Boolean {
        val sharedPreferences =
            context.getSharedPreferences("testcd", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(key, defaultValue)
    }
}