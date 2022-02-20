package com.libiao.mushroom.room.ban.three

import androidx.room.*


@Dao
interface BanThreeShareDao {

    @Query("SELECT * FROM BanThreeShareInfo")
    fun getAllShares(): MutableList<BanThreeShareInfo>

    @Query("SELECT * FROM BanThreeShareInfo WHERE code = :code")
    fun find(code: String): Boolean

    @Insert
    fun insert(info: BanThreeShareInfo): Long

    @Update
    fun update(info: BanThreeShareInfo)

    @Query("DELETE FROM BanThreeShareInfo WHERE code = :code")
    fun delete(code: String)
}