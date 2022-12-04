package com.libiao.mushroom.room.review

import androidx.room.*


@Dao
interface ReviewInfoDao {

    @Query("SELECT * FROM ReviewInfo")
    fun getShares(): MutableList<ReviewInfo>

    @Insert
    fun insert(info: ReviewInfo): Long

    @Update
    fun update(info: ReviewInfo)

    @Query("DELETE FROM ReviewInfo WHERE time = :time")
    fun delete(time: String)
}