package com.libiao.mushroom.room

import androidx.room.*


@Dao
interface Up50ShareDao {

    @Query("SELECT * FROM Up50ShareInfo")
    fun getShares(): MutableList<Up50ShareInfo>

    @Insert
    fun insert(info: Up50ShareInfo): Long

    @Update
    fun update(info: Up50ShareInfo)

    @Query("DELETE FROM Up50ShareInfo WHERE code = :code")
    fun delete(code: String)
}