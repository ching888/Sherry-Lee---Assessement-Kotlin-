package com.example.fourtitude_recipe_assessment.Recipe

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.app.AppCompatActivity
import com.example.fourtitude_recipe_assessment.R
import com.example.fourtitude_recipe_assessment.SQL.RecipeDBManager
import java.io.ByteArrayOutputStream

class CreateRecipe : AppCompatActivity(), OnItemSelectedListener {
    var foodtypes = arrayOf<String?>("Main", "Soup", "Dessert")
    var recipeName: EditText? = null
    var recipeIngredients: EditText? = null
    var recipeSteps: EditText? = null
    var btnAdd: Button? = null
    var spin: View? = null
    var widgets: View? = null
    var spinner: Spinner? = null
    var fTypesAdapter: ArrayAdapter<*>? = null
    var dbManager: RecipeDBManager? = null
    var img: ImageView? = null
    var SELECT_PICTURE = 200
    var picFile: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_recipe)
        dbManager = RecipeDBManager(this)
        img = findViewById(R.id.imgRecipe)
        img?.setOnClickListener(View.OnClickListener { imgUpload() })
        recipeName = findViewById(R.id.fName)
        recipeIngredients = findViewById(R.id.fIngredients)
        recipeSteps = findViewById(R.id.fSteps)
        widgets = findViewById(R.id.erecipe)
        widgets = findViewById(R.id.erecipe)
        spin = findViewById(R.id.spinner)

        //Calling for spinner and listen to its activity
        spinner = findViewById(R.id.ftypes)
        spinner?.setOnItemSelectedListener(this)

        //Create ArrayAdapter instance having the food type list
        fTypesAdapter = ArrayAdapter<Any?>(this, android.R.layout.simple_spinner_item, foodtypes)
        fTypesAdapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        //Set ArrayAdapter data on spinner
        spinner?.setAdapter(fTypesAdapter)
        btnAdd = findViewById(R.id.create)
        onCreateClicked()
    }

    override fun onItemSelected(arg0: AdapterView<*>?, arg1: View, position: Int, id: Long) {
        //change list
    }

    override fun onNothingSelected(arg0: AdapterView<*>?) {
        //remain all or from latest in db
    }

    fun onCreateClicked() {
        btnAdd!!.setOnClickListener {
            //add to local db
            dbManager!!.open()

            //Set image
            val drawable = img!!.drawable as BitmapDrawable
            val bitmap = drawable.bitmap
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
            val bb = baos.toByteArray()
            val imagelink = Base64.encodeToString(bb, Base64.DEFAULT)
            val name = recipeName!!.text.toString()
            val ftype = spinner!!.selectedItem.toString()
            val ingredients = recipeIngredients!!.text.toString()
            val steps = recipeSteps!!.text.toString()
            dbManager!!.insert(imagelink, name, ftype, ingredients, steps)
            dbManager!!.close()
            val intent = Intent()
            setResult(RESULT_OK, intent)
            finish() //Return to RecipeList screen
        }
    }

    fun imgUpload() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                val selectedImageUri = data!!.data
                if (selectedImageUri != null) {
                    img!!.setImageURI(selectedImageUri)
                }
            }
        }
    }
}