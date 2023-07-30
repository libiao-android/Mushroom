package com.libiao.mushroom.room

import androidx.room.*
import com.libiao.mushroom.MushRoomApplication

@Database(entities = [MineShareInfo::class, CollectShareInfo::class], version = 1,exportSchema = false)
abstract class MineTest2ShareDatabase: RoomDatabase() {

    companion object{
        private val DB_NAME = "MineTest2ShareDatabase.db"
        @Volatile
        private var instance: MineTest2ShareDatabase? = null

        @Synchronized
        internal fun getInstance(): MineTest2ShareDatabase? {
            if (instance == null) {
                instance = create()
            }
            return instance
        }

        private fun create(): MineTest2ShareDatabase {
            return Room.databaseBuilder(
                MushRoomApplication.sApplication!!,
                MineTest2ShareDatabase::class.java,
                DB_NAME
            ).allowMainThreadQueries().build()
        }
    }

    abstract fun getMineShareDao(): MineShareDao

    abstract fun getCollectShareDao(): CollectShareDao
}