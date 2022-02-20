package com.libiao.mushroom.room.ban.twochuang

import androidx.room.*


@Dao
interface BanTwoChuangShareDao {

    @Query("SELECT * FROM BanTwoChuangShareInfo")
    fun getAllShares(): MutableList<BanTwoChuangShareInfo>

    @Query("SELECT * FROM BanTwoChuangShareInfo WHERE code = :code")
    fun find(code: String): Boolean

    @Insert
    fun insert(info: BanTwoChuangShareInfo): Long

    @Update
    fun update(info: BanTwoChuangShareInfo)

    @Query("DELETE FROM BanTwoChuangShareInfo WHERE code = :code")
    fun delete(code: String)
}