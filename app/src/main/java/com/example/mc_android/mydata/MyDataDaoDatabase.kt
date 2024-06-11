package com.example.mc_android.mydata

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [MyData::class], version = 1, exportSchema = false)
abstract class MyDataDaoDatabase: RoomDatabase() {
    abstract fun myDataDao(): MyDataDao
    companion object{
        private var instance: MyDataDaoDatabase? =null
        @Synchronized
        fun getDatabase (context: Context): MyDataDaoDatabase?{
            if(instance==null){
                synchronized (MyDataDaoDatabase::class){
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        MyDataDaoDatabase::class.java,
                        "my_data"
                    ).build()
                }
            }
            return instance
        }
    }
    }