package com.libiao.mushroom.room

import androidx.room.*


@Dao
interface CollectShareDao {

    @Query("SELECT * FROM CollectShareInfo")
    fun getCollectShares(): List<CollectShareInfo>

    @Query("SELECT * FROM CollectShareInfo WHERE code = :code")
    fun find(code: String): Boolean

    @Insert
    fun insert(info: CollectShareInfo): Long

    @Update
    fun update(info: CollectShareInfo)

    @Query("DELETE FROM CollectShareInfo WHERE code = :code")
    fun delete(code: String)
}