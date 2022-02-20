package com.libiao.mushroom.room.ban

import androidx.room.*
import com.libiao.mushroom.MushRoomApplication
import com.libiao.mushroom.room.ban.five.BanFiveShareDao
import com.libiao.mushroom.room.ban.five.BanFiveShareInfo
import com.libiao.mushroom.room.ban.four.BanFourShareDao
import com.libiao.mushroom.room.ban.four.BanFourShareInfo
import com.libiao.mushroom.room.ban.one.BanOneShareDao
import com.libiao.mushroom.room.ban.one.BanOneShareInfo
import com.libiao.mushroom.room.ban.onechuang.BanOneChuangShareDao
import com.libiao.mushroom.room.ban.onechuang.BanOneChuangShareInfo
import com.libiao.mushroom.room.ban.six.BanSixShareDao
import com.libiao.mushroom.room.ban.six.BanSixShareInfo
import com.libiao.mushroom.room.ban.three.BanThreeShareDao
import com.libiao.mushroom.room.ban.three.BanThreeShareInfo
import com.libiao.mushroom.room.ban.two.BanTwoShareDao
import com.libiao.mushroom.room.ban.two.BanTwoShareInfo
import com.libiao.mushroom.room.ban.twochuang.BanTwoChuangShareDao
import com.libiao.mushroom.room.ban.twochuang.BanTwoChuangShareInfo

@Database(entities = [BanOneShareInfo::class,
    BanTwoShareInfo::class,
    BanThreeShareInfo::class,
    BanFourShareInfo::class,
    BanOneChuangShareInfo::class,
    BanTwoChuangShareInfo::class,
    BanFiveShareInfo::class,
    BanSixShareInfo::class
], version = 1, exportSchema = false)
abstract class BanShareDatabase: RoomDatabase() {

    companion object{
        private val DB_NAME = "BanDatabase.db"
        @Volatile
        private var instance: BanShareDatabase? = null

        @Synchronized
        internal fun getInstance(): BanShareDatabase? {
            if (instance == null) {
                instance = create()
            }
            return instance
        }

        private fun create(): BanShareDatabase {
            return Room.databaseBuilder(
                MushRoomApplication.sApplication!!,
                BanShareDatabase::class.java,
                DB_NAME
            ).allowMainThreadQueries().build()
        }
    }

    abstract fun getBanOneShareDao(): BanOneShareDao
    abstract fun getBanTwoShareDao(): BanTwoShareDao
    abstract fun getBanThreeShareDao(): BanThreeShareDao
    abstract fun getBanFourShareDao(): BanFourShareDao
    abstract fun getBanOneChuangShareDao(): BanOneChuangShareDao
    abstract fun getBanTwoChuangShareDao(): BanTwoChuangShareDao
    abstract fun getBanFiveShareDao(): BanFiveShareDao
    abstract fun getBanSixShareDao(): BanSixShareDao
}