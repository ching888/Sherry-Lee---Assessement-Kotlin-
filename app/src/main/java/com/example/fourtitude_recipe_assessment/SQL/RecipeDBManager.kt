package com.example.fourtitude_recipe_assessment.SQL

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase

class RecipeDBManager(private val context: Context) {
    private var helper: RecipeDatabaseHelper? = null
    private var database: SQLiteDatabase? = null
    //@kotlin.jvm.Throws(SQLException::class)
    fun open(): RecipeDBManager {
        helper = RecipeDatabaseHelper(context)
        database = helper!!.writableDatabase
        return this
    }

    fun close() {
        helper!!.close()
    }

    fun insert(img: String?, name: String?, ftype: String?, ingredients: String?, steps: String?) {
        val contentValue = ContentValues()
        contentValue.put(RecipeDatabaseHelper.IMG, img)
        contentValue.put(RecipeDatabaseHelper.NAME, name)
        contentValue.put(RecipeDatabaseHelper.FTYPE, ftype)
        contentValue.put(RecipeDatabaseHelper.INGREDIENTS, ingredients)
        contentValue.put(RecipeDatabaseHelper.STEPS, steps)
        database!!.insert(RecipeDatabaseHelper.TABLE_NAME, null, contentValue)
    }

    fun fetch(): Cursor? {
        val columns = arrayOf(
                RecipeDatabaseHelper._ID,
                RecipeDatabaseHelper.IMG,
                RecipeDatabaseHelper.NAME,
                RecipeDatabaseHelper.FTYPE,
                RecipeDatabaseHelper.INGREDIENTS,
                RecipeDatabaseHelper.STEPS)
        val cursor = database!!.query(RecipeDatabaseHelper.TABLE_NAME, columns, null, null, null, null, null)
        cursor?.moveToFirst()
        return cursor
    }

    fun fetchDataByType(fType: String): Cursor? {
        var cursor: Cursor? = null
        //food type
        cursor = database!!.rawQuery("SELECT * FROM RECIPE WHERE FTYPE=?", arrayOf(fType + ""))
        cursor?.moveToFirst()
        return cursor
    }

    fun fetchDataByID(id: Int): Cursor? {
        var cursor: Cursor? = null
        //food type
        cursor = database!!.rawQuery("SELECT * FROM RECIPE WHERE _ID=?", arrayOf(id.toString() + ""))
        cursor?.moveToFirst()
        return cursor
    }

    fun update(_id: Long, img: String?, name: String?, ftype: String?, ingredients: String?, steps: String?): Int {
        val contentValues = ContentValues()
        contentValues.put(RecipeDatabaseHelper.IMG, img)
        contentValues.put(RecipeDatabaseHelper.NAME, name)
        contentValues.put(RecipeDatabaseHelper.FTYPE, ftype)
        contentValues.put(RecipeDatabaseHelper.INGREDIENTS, ingredients)
        contentValues.put(RecipeDatabaseHelper.STEPS, steps)
        return database!!.update(RecipeDatabaseHelper.TABLE_NAME, contentValues, RecipeDatabaseHelper._ID + " = " + _id, null)
    }

    fun delete(_id: Long) {
        database!!.delete(RecipeDatabaseHelper.TABLE_NAME, RecipeDatabaseHelper._ID + "=" + _id, null)
    }

}