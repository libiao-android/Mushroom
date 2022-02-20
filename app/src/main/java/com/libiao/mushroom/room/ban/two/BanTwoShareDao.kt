package com.libiao.mushroom.room.ban.two

import androidx.room.*


@Dao
interface BanTwoShareDao {

    @Query("SELECT * FROM BanTwoShareInfo")
    fun getAllShares(): MutableList<BanTwoShareInfo>

    @Query("SELECT * FROM BanTwoShareInfo WHERE code = :code")
    fun find(code: String): Boolean

    @Insert
    fun insert(info: BanTwoShareInfo): Long

    @Update
    fun update(info: BanTwoShareInfo)

    @Query("DELETE FROM BanTwoShareInfo WHERE code = :code")
    fun delete(code: String)
}