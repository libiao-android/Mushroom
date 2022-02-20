package com.libiao.mushroom.room.ban.five

import androidx.room.*


@Dao
interface BanFiveShareDao {

    @Query("SELECT * FROM BanFiveShareInfo")
    fun getAllShares(): MutableList<BanFiveShareInfo>

    @Query("SELECT * FROM BanFiveShareInfo WHERE code = :code")
    fun find(code: String): Boolean

    @Insert
    fun insert(info: BanFiveShareInfo): Long

    @Update
    fun update(info: BanFiveShareInfo)

    @Query("DELETE FROM BanFiveShareInfo WHERE code = :code")
    fun delete(code: String)
}