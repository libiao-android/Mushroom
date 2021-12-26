package com.libiao.mushroom.room

import androidx.room.*


@Dao
interface MineShareDao {

    @Query("SELECT * FROM mineshareinfo")
    fun getMineShares(): List<MineShareInfo>

    @Insert
    fun insert(info: MineShareInfo)

    @Update
    fun update(info: MineShareInfo)

    @Query("DELETE FROM MineShareInfo WHERE code = :code")
    fun delete(code: String)
}