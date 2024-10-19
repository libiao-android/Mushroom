package com.libiao.mushroom.room

import androidx.room.*
import com.libiao.mushroom.MushRoomApplication
import com.libiao.mushroom.room.report.ReportShareDao
import com.libiao.mushroom.room.report.ReportShareInfo

@Database(entities = [ReportShareInfo::class, CollectShareInfo::class], version = 1,exportSchema = false)
abstract class XinGaoShareDatabase: RoomDatabase() {

    companion object{
        private val DB_NAME = "XinGaoShareDatabase.db"
        @Volatile
        private var instance: XinGaoShareDatabase? = null

        @Synchronized
        internal fun getInstance(): XinGaoShareDatabase? {
            if (instance == null) {
                instance = create()
            }
            return instance
        }

        private fun create(): XinGaoShareDatabase {
            return Room.databaseBuilder(
                MushRoomApplication.sApplication!!,
                XinGaoShareDatabase::class.java,
                DB_NAME
            ).allowMainThreadQueries().build()
        }
    }

    abstract fun getReportShareDao(): ReportShareDao

    abstract fun getCollectShareDao(): CollectShareDao
}