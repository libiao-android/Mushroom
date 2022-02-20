package com.libiao.mushroom.room.ban.one

import androidx.room.*


@Dao
interface BanOneShareDao {

    @Query("SELECT * FROM BanOneShareInfo")
    fun getAllShares(): MutableList<BanOneShareInfo>

    @Query("SELECT * FROM BanOneShareInfo WHERE code = :code")
    fun find(code: String): Boolean

    @Insert
    fun insert(info: BanOneShareInfo): Long

    @Update
    fun update(info: BanOneShareInfo)

    @Query("DELETE FROM BanOneShareInfo WHERE code = :code")
    fun delete(code: String)
}