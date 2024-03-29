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

    @Query("DELETE FROM TestShareInfo WHERE ext5 = :ext")
    fun deleteTest(ext: String)

    @Query("SELECT * FROM TestShareInfo WHERE ext5 = 3")
    fun getXinGao2Shares(): MutableList<TestShareInfo>

    @Query("SELECT * FROM TestShareInfo WHERE ext5 = 8")
    fun getMineTestShares(): MutableList<TestShareInfo>

    @Query("SELECT * FROM TestShareInfo WHERE ext5 = 4")
    fun getXindiShares(): MutableList<TestShareInfo>

    @Query("SELECT * FROM TestShareInfo WHERE ext5 = 11")
    fun getTwoLianBanShares(): MutableList<TestShareInfo>
}