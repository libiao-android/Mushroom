package com.libiao.mushroom.room

import androidx.room.*
import com.libiao.mushroom.MushRoomApplication

@Database(entities = [MineShareInfo::class, CollectShareInfo::class], version = 1,exportSchema = false)
abstract class MineShareDatabase: RoomDatabase() {

    companion object{
        private val DB_NAME = "MineShareDatabase.db"
        @Volatile
        private var instance: MineShareDatabase? = null

        @Synchronized
        internal fun getInstance(): MineShareDatabase? {
            if (instance == null) {
                instance = create()
            }
            return instance
        }

        private fun create(): MineShareDatabase {
            return Room.databaseBuilder(
                MushRoomApplication.sApplication!!,
                MineShareDatabase::class.java,
                DB_NAME
            ).allowMainThreadQueries().build()
        }
    }

    abstract fun getMineShareDao(): MineShareDao

    abstract fun getCollectShareDao(): CollectShareDao
}