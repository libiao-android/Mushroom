package com.libiao.mushroom.room

import androidx.room.*
import com.libiao.mushroom.MushRoomApplication

@Database(entities = [FangLiangShareInfo::class], version = 1,exportSchema = false)
abstract class FangLiangShareDatabase2: RoomDatabase() {

    companion object{
        private val DB_NAME = "FangLiangDatabase2.db"
        @Volatile
        private var instance: FangLiangShareDatabase2? = null

        @Synchronized
        internal fun getInstance(): FangLiangShareDatabase2? {
            if (instance == null) {
                instance = create()
            }
            return instance
        }

        private fun create(): FangLiangShareDatabase2 {
            return Room.databaseBuilder(
                MushRoomApplication.sApplication!!,
                FangLiangShareDatabase2::class.java,
                DB_NAME
            ).allowMainThreadQueries().build()
        }
    }

    abstract fun getFangLiangShareDao(): FangLiangShareDao
}