package com.libiao.mushroom.room.ban.four

import androidx.room.*


@Dao
interface BanFourShareDao {

    @Query("SELECT * FROM BanFourShareInfo")
    fun getAllShares(): MutableList<BanFourShareInfo>

    @Query("SELECT * FROM BanFourShareInfo WHERE code = :code")
    fun find(code: String): Boolean

    @Insert
    fun insert(info: BanFourShareInfo): Long

    @Update
    fun update(info: BanFourShareInfo)

    @Query("DELETE FROM BanFourShareInfo WHERE code = :code")
    fun delete(code: String)
}