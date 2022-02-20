package com.libiao.mushroom.room.ban.onechuang

import androidx.room.*


@Dao
interface BanOneChuangShareDao {

    @Query("SELECT * FROM BanOneChuangShareInfo")
    fun getAllShares(): MutableList<BanOneChuangShareInfo>

    @Query("SELECT * FROM BanOneChuangShareInfo WHERE code = :code")
    fun find(code: String): Boolean

    @Insert
    fun insert(info: BanOneChuangShareInfo): Long

    @Update
    fun update(info: BanOneChuangShareInfo)

    @Query("DELETE FROM BanOneChuangShareInfo WHERE code = :code")
    fun delete(code: String)
}