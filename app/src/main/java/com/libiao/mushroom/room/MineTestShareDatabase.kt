package com.libiao.mushroom.room

import androidx.room.*
import com.libiao.mushroom.MushRoomApplication

@Database(entities = [MineShareInfo::class, CollectShareInfo::class], version = 1,exportSchema = false)
abstract class MineTestShareDatabase: RoomDatabase() {

    companion object{
        private val DB_NAME = "MineTestShareDatabase.db"
        @Volatile
        private var instance: MineTestShareDatabase? = null

        @Synchronized
        internal fun getInstance(): MineTestShareDatabase? {
            if (instance == null) {
                instance = create()
            }
            return instance
        }

        private fun create(): MineTestShareDatabase {
            return Room.databaseBuilder(
                MushRoomApplication.sApplication!!,
                MineTestShareDatabase::class.java,
                DB_NAME
            ).allowMainThreadQueries().build()
        }
    }

    abstract fun getMineShareDao(): MineShareDao

    abstract fun getCollectShareDao(): CollectShareDao
}