package com.libiao.mushroom.room

import androidx.room.*
import com.libiao.mushroom.MushRoomApplication

@Database(entities = [MineShareInfo::class], version = 1,exportSchema = false)
abstract class ChuangDatabase: RoomDatabase() {

    companion object{
        private val DB_NAME = "ChuangDatabase.db"
        @Volatile
        private var instance: ChuangDatabase? = null

        @Synchronized
        internal fun getInstance(): ChuangDatabase? {
            if (instance == null) {
                instance = create()
            }
            return instance
        }

        private fun create(): ChuangDatabase {
            return Room.databaseBuilder(
                MushRoomApplication.sApplication!!,
                ChuangDatabase::class.java,
                DB_NAME
            ).allowMainThreadQueries().build()
        }
    }

    abstract fun getMineShareDao(): MineShareDao
}