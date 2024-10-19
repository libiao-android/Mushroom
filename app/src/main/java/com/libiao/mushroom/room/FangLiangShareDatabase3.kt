package com.libiao.mushroom.room

import androidx.room.*
import com.libiao.mushroom.MushRoomApplication

@Database(entities = [FangLiangShareInfo::class], version = 1,exportSchema = false)
abstract class FangLiangShareDatabase3: RoomDatabase() {

    companion object{
        private val DB_NAME = "FangLiangDatabase3.db"
        @Volatile
        private var instance: FangLiangShareDatabase3? = null

        @Synchronized
        internal fun getInstance(): FangLiangShareDatabase3? {
            if (instance == null) {
                instance = create()
            }
            return instance
        }

        private fun create(): FangLiangShareDatabase3 {
            return Room.databaseBuilder(
                MushRoomApplication.sApplication!!,
                FangLiangShareDatabase3::class.java,
                DB_NAME
            ).allowMainThreadQueries().build()
        }
    }

    abstract fun getFangLiangShareDao(): FangLiangShareDao
}