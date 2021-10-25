package com.example.guessthephrasesql


import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper (context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object{
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "phrases.db"
        private const val TABLE_NOTES = "Phrases"

        private const val KEY_PHRASE = "Phrase"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE $TABLE_NOTES ($KEY_PHRASE TEXT)")
    }

    fun addPhrase(phrase: String): Long{
        val sqLiteDatabase = writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_PHRASE, phrase)
        val success = sqLiteDatabase.insert(TABLE_NOTES,null, contentValues)
        sqLiteDatabase.close()
        return success
    }

    fun retrieveAllPhrase(): ArrayList<String>{
        val sqLiteDatabase = writableDatabase
        var list = arrayListOf<String>()
        val cursor : Cursor = sqLiteDatabase.query(TABLE_NOTES, null,null,null,null,null,null)
        if (cursor.moveToFirst()) {
            do {
                val phrase = cursor.getString(cursor.getColumnIndex(KEY_PHRASE))
                list.add(phrase)
            } while (cursor.moveToNext());
        }
//        sqLiteDatabase.close()
        return list
    }


    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_NOTES")
        onCreate(db)
    }

    fun reset() {
        writableDatabase!!.execSQL("DROP TABLE IF EXISTS $TABLE_NOTES")
    }
}