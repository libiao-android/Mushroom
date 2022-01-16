package com.libiao.mushroom.room

import androidx.room.*
import com.libiao.mushroom.MushRoomApplication

@Database(entities = [FangLiangShareInfo::class], version = 1,exportSchema = false)
abstract class FangLiangShareDatabase: RoomDatabase() {

    companion object{
        private val DB_NAME = "FangLiangDatabase.db"
        @Volatile
        private var instance: FangLiangShareDatabase? = null

        @Synchronized
        internal fun getInstance(): FangLiangShareDatabase? {
            if (instance == null) {
                instance = create()
            }
            return instance
        }

        private fun create(): FangLiangShareDatabase {
            return Room.databaseBuilder(
                MushRoomApplication.sApplication!!,
                FangLiangShareDatabase::class.java,
                DB_NAME
            ).allowMainThreadQueries().build()
        }
    }

    abstract fun getFangLiangShareDao(): FangLiangShareDao
}