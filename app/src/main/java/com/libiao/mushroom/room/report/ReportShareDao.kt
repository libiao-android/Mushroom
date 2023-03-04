package com.libiao.mushroom.room.report

import androidx.room.*


@Dao
interface ReportShareDao {

    @Query("SELECT * FROM ReportShareInfo")
    fun getShares(): MutableList<ReportShareInfo>

    @Insert
    fun insert(info: ReportShareInfo): Long

    @Update
    fun update(info: ReportShareInfo)

    @Query("DELETE FROM ReportShareInfo WHERE code = :code")
    fun delete(code: String)

    @Query("DELETE FROM ReportShareInfo WHERE ext5 = :ext")
    fun deleteTest(ext: String)
}