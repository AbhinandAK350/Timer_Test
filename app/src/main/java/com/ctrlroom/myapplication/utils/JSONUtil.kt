package com.ctrlroom.myapplication.utils

import android.content.Context
import java.io.*

object JSONUtil {
    @Throws(IOException::class)
    fun objectToFile(context: Context, obj: Any?): String {
        val dir = File(context.filesDir, "logs")
        if (!dir.exists()) {
            dir.mkdirs()
        }

        val data = File(context.filesDir, "logs/api.json")
        if (!data.createNewFile()) {
            data.delete()
            data.createNewFile()
        }
        val objectOutputStream = ObjectOutputStream(FileOutputStream(data))
        objectOutputStream.writeObject(obj)
        objectOutputStream.close()
        return File(context.filesDir, "logs").path
    }

    @Throws(IOException::class, ClassNotFoundException::class)
    fun objectFromFile(path: String?): Any? {
        var `object`: Any? = null
        val data = File(path)
        if (data.exists()) {
            val objectInputStream = ObjectInputStream(FileInputStream(data))
            `object` = objectInputStream.readObject()
            objectInputStream.close()
        }
        return `object`
    }
}