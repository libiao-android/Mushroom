package com.libiao.mushroom.room.ban.six

import androidx.room.*


@Dao
interface BanSixShareDao {

    @Query("SELECT * FROM BanSixShareInfo")
    fun getAllShares(): MutableList<BanSixShareInfo>

    @Query("SELECT * FROM BanSixShareInfo WHERE code = :code")
    fun find(code: String): Boolean

    @Insert
    fun insert(info: BanSixShareInfo): Long

    @Update
    fun update(info: BanSixShareInfo)

    @Query("DELETE FROM BanSixShareInfo WHERE code = :code")
    fun delete(code: String)
}