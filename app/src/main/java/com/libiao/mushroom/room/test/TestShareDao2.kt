package com.libiao.mushroom.room.test

import androidx.room.*
import com.libiao.mushroom.room.TestShareInfo


@Dao
interface TestShareDao2 {

    @Query("SELECT * FROM TestShareInfo")
    fun getShares(): MutableList<TestShareInfo>

    @Insert
    fun insert(info: TestShareInfo): Long

    @Update
    fun update(info: TestShareInfo)

    @Query("DELETE FROM TestShareInfo WHERE code = :code")
    fun delete(code: String)
}