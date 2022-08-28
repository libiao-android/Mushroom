package com.libiao.mushroom.room.jigou

import androidx.room.*
import com.libiao.mushroom.MushRoomApplication

@Database(entities = [JiGouShareInfo::class], version = 1,exportSchema = false)
abstract class JiGouShareDatabase: RoomDatabase() {

    companion object{
        private val DB_NAME = "JiGouShareDatabase.db"
        @Volatile
        private var instance: JiGouShareDatabase? = null

        @Synchronized
        internal fun getInstance(): JiGouShareDatabase? {
            if (instance == null) {
                instance = create()
            }
            return instance
        }

        private fun create(): JiGouShareDatabase {
            return Room.databaseBuilder(
                MushRoomApplication.sApplication!!,
                JiGouShareDatabase::class.java,
                DB_NAME
            ).allowMainThreadQueries().build()
        }
    }

    abstract fun getJiGouShareDao(): JiGouShareDao

}