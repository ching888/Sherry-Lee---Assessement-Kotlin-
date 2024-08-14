package com.example.fourtitude_recipe_assessment.SQL

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class RecipeDatabaseHelper(context: Context?) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME)
        onCreate(db)
    }

    companion object {
        const val TABLE_NAME = "RECIPE"
        const val _ID = "_id"
        const val IMG = "image"
        const val NAME = "name"
        const val FTYPE = "ftype"
        const val INGREDIENTS = "ingredients"
        const val STEPS = "steps"
        const val DB_NAME = "RECIPE.DB"
        const val DB_VERSION = 1
        private const val CREATE_TABLE = ("create table " + TABLE_NAME + "(" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + IMG + " TEXT, " + NAME + " TEXT NOT NULL, " + FTYPE + " TEXT NOT NULL, " + INGREDIENTS + " TEXT, " + STEPS + " TEXT);")
    }
}