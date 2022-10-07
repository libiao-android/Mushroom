package com.libiao.mushroom.utils

import java.io.*

object FileUtil {

    fun writeFileAppend(file: File, info: String, lineFeed: Boolean = true) {
        var fileWriter: FileWriter? = null
        try {
            fileWriter = FileWriter(file, true)
            if(lineFeed) {
                fileWriter.append("$info\n")
            } else {
                fileWriter.append(info)
            }
            fileWriter.flush()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun copy(source: File, target: File) {
        var fileInputStream: FileInputStream?  = null
        var fileOutputStream: FileOutputStream? = null
        try {
            fileInputStream = FileInputStream(source);
            fileOutputStream = FileOutputStream(target);
            val buffer = ByteArray(1024)
            while (fileInputStream.read(buffer) > 0) {
                fileOutputStream.write(buffer);
            }
        } catch (e: Exception) {
            e.printStackTrace();
        } finally {
            try {
                source.delete()
                fileInputStream?.close();
                fileOutputStream?.close();
            } catch (e: IOException) {
                e.printStackTrace();
            }
        }
    }
}