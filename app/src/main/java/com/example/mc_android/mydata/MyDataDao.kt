package com.example.mc_android.mydata

import androidx.room.Dao
import androidx.room.Insert

@Dao
interface MyDataDao {
    @Insert
    suspend fun insert(myData: MyData)
}