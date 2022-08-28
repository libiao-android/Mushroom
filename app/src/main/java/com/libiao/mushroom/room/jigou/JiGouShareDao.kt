package com.libiao.mushroom.room.jigou

import androidx.room.*


@Dao
interface JiGouShareDao {

    @Query("SELECT * FROM JiGouShareInfo")
    fun getShares(): MutableList<JiGouShareInfo>

    @Insert
    fun insert(info: JiGouShareInfo): Long

    @Update
    fun update(info: JiGouShareInfo)

    @Query("DELETE FROM JiGouShareInfo WHERE code = :code")
    fun delete(code: String)
}