package com.libiao.mushroom.room

import androidx.room.*
import com.libiao.mushroom.MushRoomApplication

@Database(entities = [TestShareInfo::class], version = 1,exportSchema = false)
abstract class TestShareDatabase: RoomDatabase() {

    companion object{
        private val DB_NAME = "TestShareDatabase.db"
        @Volatile
        private var instance: TestShareDatabase? = null

        @Synchronized
        internal fun getInstance(): TestShareDatabase? {
            if (instance == null) {
                instance = create()
            }
            return instance
        }

        private fun create(): TestShareDatabase {
            return Room.databaseBuilder(
                MushRoomApplication.sApplication!!,
                TestShareDatabase::class.java,
                DB_NAME
            ).allowMainThreadQueries().build()
        }
    }

    abstract fun getTestShareDao(): TestShareDao

}