package com.libiao.mushroom.room

import androidx.room.*


@Dao
interface TestShareDao {

    @Query("SELECT * FROM TestShareInfo")
    fun getShares(): MutableList<TestShareInfo>

    @Insert
    fun insert(info: TestShareInfo): Long

    @Update
    fun update(info: TestShareInfo)

    @Query("DELETE FROM TestShareInfo WHERE code = :code")
    fun delete(code: String)
}