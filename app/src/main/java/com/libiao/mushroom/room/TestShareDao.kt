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

    @Query("SELECT * FROM TestShareInfo WHERE ext5 = 1")
    fun getDaZhangShares1(): MutableList<TestShareInfo>

    @Query("SELECT * FROM TestShareInfo WHERE ext5 = 2")
    fun getDaZhangShares2(): MutableList<TestShareInfo>

    @Query("SELECT * FROM TestShareInfo WHERE ext5 = 111")
    fun getDaZhangShares3(): MutableList<TestShareInfo>

    @Query("SELECT * FROM TestShareInfo WHERE ext5 = 8888")
    fun getZhenDang(): MutableList<TestShareInfo>
}