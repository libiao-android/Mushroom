package com.libiao.mushroom.room.review

import androidx.room.*
import com.libiao.mushroom.MushRoomApplication

@Database(entities = [ReviewInfo::class], version = 1,exportSchema = false)
@TypeConverters(MapTypeConverter::class)
abstract class ReviewInfoDatabase: RoomDatabase() {

    companion object{
        private val DB_NAME = "ReviewInfoDatabase.db"
        @Volatile
        private var instance: ReviewInfoDatabase? = null

        @Synchronized
        internal fun getInstance(): ReviewInfoDatabase? {
            if (instance == null) {
                instance = create()
            }
            return instance
        }

        private fun create(): ReviewInfoDatabase {
            return Room.databaseBuilder(
                MushRoomApplication.sApplication!!,
                ReviewInfoDatabase::class.java,
                DB_NAME
            ).allowMainThreadQueries().build()
        }
    }

    abstract fun getReviewDao(): ReviewInfoDao
}