package com.libiao.mushroom.room

import androidx.room.*


@Dao
interface TestOneShareDao {

    @Query("SELECT * FROM testoneshareinfo")
    fun getMineShares(): List<TestOneShareInfo>

    @Insert
    fun insert(info: TestOneShareInfo): Long

    @Update
    fun update(info: TestOneShareInfo)

    @Query("DELETE FROM TestOneShareInfo WHERE code = :code")
    fun delete(code: String)
}