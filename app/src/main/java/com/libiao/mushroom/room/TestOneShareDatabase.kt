package com.libiao.mushroom.room

import androidx.room.*
import com.libiao.mushroom.MushRoomApplication

@Database(entities = [TestOneShareInfo::class], version = 1,exportSchema = false)
abstract class TestOneShareDatabase: RoomDatabase() {

    companion object{
        private val DB_NAME = "TestOneShareDatabase.db"
        @Volatile
        private var instance: TestOneShareDatabase? = null

        @Synchronized
        internal fun getInstance(): TestOneShareDatabase? {
            if (instance == null) {
                instance = create()
            }
            return instance
        }

        private fun create(): TestOneShareDatabase {
            return Room.databaseBuilder(
                MushRoomApplication.sApplication!!,
                TestOneShareDatabase::class.java,
                DB_NAME
            ).allowMainThreadQueries().build()
        }
    }

    abstract fun getMineShareDao(): TestOneShareDao

}