package com.libiao.mushroom.room

import androidx.room.*
import com.libiao.mushroom.MushRoomApplication

@Database(entities = [Up50ShareInfo::class], version = 1,exportSchema = false)
abstract class Up50ShareDatabase: RoomDatabase() {

    companion object{
        private val DB_NAME = "Up50ShareDatabase.db"
        @Volatile
        private var instance: Up50ShareDatabase? = null

        @Synchronized
        internal fun getInstance(): Up50ShareDatabase? {
            if (instance == null) {
                instance = create()
            }
            return instance
        }

        private fun create(): Up50ShareDatabase {
            return Room.databaseBuilder(
                MushRoomApplication.sApplication!!,
                Up50ShareDatabase::class.java,
                DB_NAME
            ).allowMainThreadQueries().build()
        }
    }

    abstract fun getUp50ShareDao(): Up50ShareDao

}