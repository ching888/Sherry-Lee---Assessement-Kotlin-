package com.example.fourtitude_recipe_assessment.Recipe

import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.example.fourtitude_recipe_assessment.Model.Recipe
import com.example.fourtitude_recipe_assessment.R
import com.example.fourtitude_recipe_assessment.SQL.RecipeDBManager
import com.example.fourtitude_recipe_assessment.SQL.RecipeDatabaseHelper
import com.google.android.material.floatingactionbutton.FloatingActionButton
import okhttp3.*
import org.json.JSONArray
import org.json.JSONException
import java.io.IOException
import java.util.*

class RecipeList : AppCompatActivity(), OnItemSelectedListener {
    var foodtypes = arrayOf<String?>("All", "Main", "Soup", "Dessert")
    var fTypes: Spinner? = null
    var fTypesAdapter: ArrayAdapter<*>? = null
    var fullRecipeList: ListView? = null
    var recipeList: MutableList<Recipe?>? = null
    var layout: View? = null
    var spin: View? = null
    var dbManager: RecipeDBManager? = null
    var cursor: Cursor? = null
    var isFirstRun: Boolean? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_list)
        recipeList = ArrayList()
        dbManager = RecipeDBManager(this)
        dbManager!!.open()
        isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("isFirstRun", true)
        if (isFirstRun!!) {
            initializeData()
            getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit().putBoolean("isFirstRun", false).commit()
        }

        cursor = dbManager!!.fetch()
        if (cursor != null && cursor!!.count > 0) {
            for (i in 0 until cursor!!.count) {
                val id = cursor!!.getString(cursor!!.getColumnIndex(RecipeDatabaseHelper._ID)).toInt()
                val img = cursor!!.getString(cursor!!.getColumnIndex(RecipeDatabaseHelper.IMG))
                val name = cursor!!.getString(cursor!!.getColumnIndex(RecipeDatabaseHelper.NAME))
                val ftype = cursor!!.getString(cursor!!.getColumnIndex(RecipeDatabaseHelper.FTYPE))
                val ingredients = cursor!!.getString(cursor!!.getColumnIndex(RecipeDatabaseHelper.INGREDIENTS))
                val steps = cursor!!.getString(cursor!!.getColumnIndex(RecipeDatabaseHelper.STEPS))
                val recipe = Recipe(id, img, name, ftype, ingredients, steps)
                recipeList?.add(recipe)
                cursor!!.moveToNext()
            }
        }

        //Create button
        val addRecipe = findViewById<FloatingActionButton>(R.id.add)
        addRecipe.setOnClickListener { //Go to CreateRecipe screen
            addRecipeToList()
        }
        spin = findViewById(R.id.spinner)

        //Calling for spinner and listen to its activity
        fTypes = findViewById(R.id.ftypes)
        fTypes?.setOnItemSelectedListener(this)

        //Create ArrayAdapter instance having the food type list
        fTypesAdapter = ArrayAdapter<Any?>(this, android.R.layout.simple_spinner_item, foodtypes)
        fTypesAdapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        //Set ArrayAdapter data on spinner
        fTypes?.setAdapter(fTypesAdapter)
        layout = findViewById(R.id.flist)
        fullRecipeList = findViewById(R.id.rlist)
        val recipeAdapter = RecipeAdapter(this, R.layout.recipe_listview, recipeList)
        fullRecipeList?.setAdapter(recipeAdapter)
    }

    override fun onItemSelected(arg0: AdapterView<*>?, arg1: View, position: Int, id: Long) {
        cursor = null
        //change list
        val foodtype = fTypes!!.selectedItem.toString()
        cursor = if (position != 0) {
            dbManager!!.fetchDataByType(foodtype)
        } else {
            dbManager!!.fetch()
        }
        recipeList = ArrayList()
        if (cursor != null) {
            for (i in 0 until cursor!!.count) {
                val fid = cursor!!.getString(cursor!!.getColumnIndex(RecipeDatabaseHelper._ID)).toInt()
                val img = cursor!!.getString(cursor!!.getColumnIndex(RecipeDatabaseHelper.IMG))
                val name = cursor!!.getString(cursor!!.getColumnIndex(RecipeDatabaseHelper.NAME))
                val ftype = cursor!!.getString(cursor!!.getColumnIndex(RecipeDatabaseHelper.FTYPE))
                val ingredients = cursor!!.getString(cursor!!.getColumnIndex(RecipeDatabaseHelper.INGREDIENTS))
                val steps = cursor!!.getString(cursor!!.getColumnIndex(RecipeDatabaseHelper.STEPS))
                val recipe = Recipe(fid, img, name, ftype, ingredients, steps)
                recipeList?.add(recipe)
                cursor!!.moveToNext()
                val recipeAdapter = RecipeAdapter(this, R.layout.recipe_listview, recipeList)
                fullRecipeList!!.adapter = recipeAdapter
            }
        }
    }

    override fun onNothingSelected(arg0: AdapterView<*>?) {
        //remain all or from latest in db
    }

    fun addRecipeToList() {
        val addScreen = Intent(this, CreateRecipe::class.java)
        startActivityForResult(addScreen, 201)
    }

    fun initializeData() {
        val recipeAPI = "https://mocki.io/v1/e76f714d-cae5-4254-ba4e-561f09b558b0"
        val client = OkHttpClient()
        val getRequest = Request.Builder().url(recipeAPI).build()
        client.newCall(getRequest).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {}

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful && response.body() != null) {
                    val responseData = response.body().string()
                    println("Response Data: $responseData")
                    try {
                        val jsonArray = JSONArray(responseData)
                        println("Array length: " + jsonArray.length())
                        for (i in 0 until jsonArray.length()) {
                            val `object` = jsonArray.getJSONObject(i)
                            val imageLink = `object`.getString("img")
                            val name = `object`.getString("name")
                            println("AAAA: $name")
                            val rtype = `object`.getString("type")
                            val ingredients = `object`.getString("ingredients")
                            val steps = `object`.getString("ps")
                            dbManager!!.insert(imageLink, name, rtype, ingredients, steps)
                        }
                    } catch (e: JSONException) {
                        println(e)
                    }
                }
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 201 && resultCode == RESULT_OK && data != null) {
            cursor = dbManager!!.fetch()
            cursor!!.moveToLast()
            val fid = cursor!!.getString(cursor!!.getColumnIndex(RecipeDatabaseHelper._ID)).toInt()
            val img = cursor!!.getString(cursor!!.getColumnIndex(RecipeDatabaseHelper.IMG))
            val name = cursor!!.getString(cursor!!.getColumnIndex(RecipeDatabaseHelper.NAME))
            val ftype = cursor!!.getString(cursor!!.getColumnIndex(RecipeDatabaseHelper.FTYPE))
            val ingredients = cursor!!.getString(cursor!!.getColumnIndex(RecipeDatabaseHelper.INGREDIENTS))
            val steps = cursor!!.getString(cursor!!.getColumnIndex(RecipeDatabaseHelper.STEPS))
            val recipe = Recipe(fid, img, name, ftype, ingredients, steps)
            recipeList!!.add(recipe)
            val recipeAdapter = RecipeAdapter(this, R.layout.recipe_listview, recipeList)
            fullRecipeList!!.adapter = recipeAdapter
        } else if (requestCode == 202 && resultCode == RESULT_OK && data != null) {
            val isUpdated = data.getBooleanExtra("updated", false)
            val isRemoved = data.getBooleanExtra("removed", false)
            if (isUpdated && !isRemoved) {
                val foodtype = fTypes!!.selectedItem.toString()
                cursor = null
                cursor = if (foodtype === "All") {
                    dbManager!!.fetch()
                } else {
                    dbManager!!.fetchDataByType(foodtype)
                }
                if (cursor != null) {
                    recipeList = ArrayList()
                    for (i in 0 until cursor!!.count) {
                        val fid = cursor!!.getString(cursor!!.getColumnIndex(RecipeDatabaseHelper._ID)).toInt()
                        val img = cursor!!.getString(cursor!!.getColumnIndex(RecipeDatabaseHelper.IMG))
                        val name = cursor!!.getString(cursor!!.getColumnIndex(RecipeDatabaseHelper.NAME))
                        val ftype = cursor!!.getString(cursor!!.getColumnIndex(RecipeDatabaseHelper.FTYPE))
                        val ingredients = cursor!!.getString(cursor!!.getColumnIndex(RecipeDatabaseHelper.INGREDIENTS))
                        val steps = cursor!!.getString(cursor!!.getColumnIndex(RecipeDatabaseHelper.STEPS))
                        val recipe = Recipe(fid, img, name, ftype, ingredients, steps)
                        recipeList?.add(recipe)
                        cursor!!.moveToNext()
                        val recipeAdapter = RecipeAdapter(this, R.layout.recipe_listview, recipeList)
                        fullRecipeList!!.adapter = recipeAdapter
                    }
                }
            } else if (!isUpdated && isRemoved) {
                val id = data.getIntExtra("update_id", 0)
                var pos = 0
                for (i in recipeList!!.indices) {
                    if (recipeList!![i]!!.id == id) {
                        pos = i
                        break
                    }
                }
                recipeList!!.removeAt(pos)
                val recipeAdapter = RecipeAdapter(this, R.layout.recipe_listview, recipeList)
                fullRecipeList!!.adapter = recipeAdapter
            }
        }
    }
}