package com.libiao.mushroom.utils

import java.io.File
import java.io.FileWriter
import java.io.IOException

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
}