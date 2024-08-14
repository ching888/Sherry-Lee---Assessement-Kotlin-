package com.example.fourtitude_recipe_assessment.Recipe

import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.app.AppCompatActivity
import com.example.fourtitude_recipe_assessment.Background.DownloadImageTask
import com.example.fourtitude_recipe_assessment.R
import com.example.fourtitude_recipe_assessment.SQL.RecipeDBManager
import com.example.fourtitude_recipe_assessment.SQL.RecipeDatabaseHelper
import java.io.ByteArrayOutputStream

class ViewRecipe : AppCompatActivity(), OnItemSelectedListener {
    var foodtypes = arrayOf<String?>("Main", "Soup", "Dessert")
    var txtrName: EditText? = null
    var txtrIngredients: EditText? = null
    var txtrSteps: EditText? = null
    var btnUpdate: Button? = null
    var btnDelete: Button? = null
    var id = 0
    var widgets: View? = null
    var spin: View? = null
    var spinner: Spinner? = null
    var fTypesAdapter: ArrayAdapter<*>? = null
    var dbManager: RecipeDBManager? = null
    var cursor: Cursor? = null
    var img: ImageView? = null
    var SELECT_PICTURE = 200
    var pic: String? = null
    var name: String? = null
    var ftype: String? = null
    var ingredients: String? = null
    var steps: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_recipe)
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
        img = findViewById(R.id.imgRecipe)
        img?.setOnClickListener(View.OnClickListener { imgUpload() })
        txtrName = findViewById(R.id.fName)
        txtrIngredients = findViewById(R.id.fIngredients)
        txtrSteps = findViewById(R.id.fSteps)
        btnUpdate = findViewById(R.id.update)
        btnDelete = findViewById(R.id.delete)
        dbManager = RecipeDBManager(this)
        dbManager!!.open()
        val extras = intent.extras
        if (extras != null) {
            id = extras.getInt("recipe_id")
            cursor = dbManager!!.fetchDataByID(id)
            if (cursor != null) {
                pic = cursor!!.getString(cursor!!.getColumnIndex(RecipeDatabaseHelper.IMG))
                name = cursor!!.getString(cursor!!.getColumnIndex(RecipeDatabaseHelper.NAME))
                ftype = cursor!!.getString(cursor!!.getColumnIndex(RecipeDatabaseHelper.FTYPE))
                ingredients = cursor!!.getString(cursor!!.getColumnIndex(RecipeDatabaseHelper.INGREDIENTS))
                steps = cursor!!.getString(cursor!!.getColumnIndex(RecipeDatabaseHelper.STEPS))
                if (pic!!.isNotEmpty()) {
                    if (pic!!.contains("http")) {
                        DownloadImageTask(img!!).execute(pic)
                    } else {
                        val imageBytes = Base64.decode(pic, Base64.DEFAULT)
                        val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                        img?.setImageBitmap(decodedImage)
                    }
                }
                txtrName?.setText(name)
                txtrIngredients?.setText(ingredients)
                txtrSteps?.setText(steps)
                val spinnerPosition = (fTypesAdapter as ArrayAdapter<Any?>).getPosition(ftype)
                spinner?.setSelection(spinnerPosition)
            }
        }
        btnUpdate?.setOnClickListener(View.OnClickListener { //Set image
            val drawable = img?.getDrawable() as BitmapDrawable
            val bitmap = drawable.bitmap
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
            val bb = baos.toByteArray()
            val imagelink = Base64.encodeToString(bb, 0)
            //update to db
            dbManager!!.update(id.toLong(), imagelink, txtrName?.getText().toString(), spinner?.getSelectedItem().toString(), txtrIngredients?.getText().toString(), txtrSteps?.getText().toString())
            val intent = Intent()
            intent.putExtra("updated", true)
            intent.putExtra("removed", false)
            setResult(RESULT_OK, intent)
            finish()
        })
        btnDelete?.setOnClickListener(View.OnClickListener {
            dbManager!!.delete(id.toLong())
            val intent = Intent()
            intent.putExtra("updated", false)
            intent.putExtra("removed", true)
            intent.putExtra("update_id", id)
            setResult(RESULT_OK, intent)
            finish()
        })
    }

    override fun onItemSelected(arg0: AdapterView<*>?, arg1: View, position: Int, id: Long) {
        //change list
    }

    override fun onNothingSelected(arg0: AdapterView<*>?) {
        //remain all or from latest in db
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