package com.libiao.mushroom.room.test

import androidx.room.*
import com.libiao.mushroom.MushRoomApplication
import com.libiao.mushroom.room.TestShareInfo

@Database(entities = [TestShareInfo::class], version = 1,exportSchema = false)
abstract class TestShareDatabase2: RoomDatabase() {

    companion object{
        private val DB_NAME = "Test2ShareDatabase.db"
        @Volatile
        private var instance: TestShareDatabase2? = null

        @Synchronized
        internal fun getInstance(): TestShareDatabase2? {
            if (instance == null) {
                instance = create()
            }
            return instance
        }

        private fun create(): TestShareDatabase2 {
            return Room.databaseBuilder(
                MushRoomApplication.sApplication!!,
                TestShareDatabase2::class.java,
                DB_NAME
            ).allowMainThreadQueries().build()
        }
    }

    abstract fun getTestShareDao(): TestShareDao2

}