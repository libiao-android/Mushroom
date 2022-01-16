package com.libiao.mushroom.room

import androidx.room.*


@Dao
interface FangLiangShareDao {

    @Query("SELECT * FROM FangLiangShareInfo")
    fun getFangLiangShares(): MutableList<FangLiangShareInfo>

    @Query("SELECT * FROM FangLiangShareInfo WHERE code = :code")
    fun find(code: String): Boolean

    @Insert
    fun insert(info: FangLiangShareInfo): Long

    @Update
    fun update(info: FangLiangShareInfo)

    @Query("DELETE FROM FangLiangShareInfo WHERE code = :code")
    fun delete(code: String)
}