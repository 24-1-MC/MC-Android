package com.example.mc_android.mydata

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MyDataDao {
    @Insert
    suspend fun insert(myData: MyData)

    @Query("SELECT * from my_data")
    fun selectAll(): LiveData<List<MyData>>
}