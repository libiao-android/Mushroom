package com.libiao.mushroom.room.report

import androidx.room.*
import com.libiao.mushroom.MushRoomApplication

@Database(entities = [ReportShareInfo::class], version = 1,exportSchema = false)
abstract class ReportShareDatabase: RoomDatabase() {

    companion object{
        private val DB_NAME = "ReportShareDatabase.db"
        @Volatile
        private var instance: ReportShareDatabase? = null

        @Synchronized
        internal fun getInstance(): ReportShareDatabase? {
            if (instance == null) {
                instance = create()
            }
            return instance
        }

        private fun create(): ReportShareDatabase {
            return Room.databaseBuilder(
                MushRoomApplication.sApplication!!,
                ReportShareDatabase::class.java,
                DB_NAME
            ).allowMainThreadQueries().build()
        }
    }

    abstract fun getReportShareDao(): ReportShareDao
}