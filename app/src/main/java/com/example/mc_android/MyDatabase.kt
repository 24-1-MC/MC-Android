package com.example.dbpracticeapplication

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import android.util.Log

class MyDatabase
{
    object MyDBContract {
        object MyEntry : BaseColumns {
            const val TABLE_NAME = "myDBfile"
            const val date = "date"
            const val startTime = "start_time"
            const val endTime = "end_time"
            const val time = "time"
        }
    }
    class MyDbHelper
        (context: Context) : SQLiteOpenHelper
        (context, DATABASE_NAME, null, DATABASE_VERSION) {
        val SQL_CREATE_ENTRIES  = "CREATE TABLE ${MyDBContract.MyEntry.TABLE_NAME} (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                "${MyDBContract.MyEntry.date} TEXT," +
                "${MyDBContract.MyEntry.startTime} TEXT," +
                "${MyDBContract.MyEntry.endTime} TEXT," +
                "${MyDBContract.MyEntry.time} TEXT)"
        val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ${MyDBContract.MyEntry.TABLE_NAME}"
        override fun onCreate (db: SQLiteDatabase) {
            db.execSQL(SQL_CREATE_ENTRIES)
        }
        override fun onUpgrade (db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            db.execSQL(SQL_DELETE_ENTRIES)
            onCreate(db)
        }
        override fun onDowngrade
                    (db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            onUpgrade(db, oldVersion, newVersion)
        }
        companion object
        {
            const val DATABASE_VERSION = 1
            const val DATABASE_NAME = "myDBfile.db"
        }
        fun selectAll(): MutableList<MyElement> {
            val readList = mutableListOf<MyElement>()
            val db = readableDatabase
            val cursor = db.rawQuery("SELECT * FROM "+ MyDBContract.MyEntry.TABLE_NAME+";", null)
            Log.d("TAG","Select All Query: "+"SELECT * FROM "+ MyDBContract.MyEntry.TABLE_NAME+";")
            Log.d("TAG",cursor.toString())
            with(cursor){
                while(moveToNext()){
                    readList.add(MyElement(
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4)
                    ))
                }
            }
            cursor.close()
            db.close()
            return readList
        }
    }
}