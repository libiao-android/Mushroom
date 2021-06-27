package com.libiao.mushroom.utils

import java.io.File
import java.io.FileWriter
import java.io.IOException

object FileUtil {

    fun writeFileAppend(file: File, info: String) {
        var fileWriter: FileWriter? = null
        try {
            fileWriter = FileWriter(file, true)
            fileWriter.append(info)
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
}