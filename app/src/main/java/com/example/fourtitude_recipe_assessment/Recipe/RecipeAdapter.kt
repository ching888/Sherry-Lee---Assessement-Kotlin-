package com.example.fourtitude_recipe_assessment.Recipe

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.fourtitude_recipe_assessment.Background.DownloadImageTask
import com.example.fourtitude_recipe_assessment.Model.Recipe
import com.example.fourtitude_recipe_assessment.R

class RecipeAdapter(var mContext: Context, resource: Int, recipies: List<Recipe?>?) : ArrayAdapter<Recipe?>(mContext, resource, recipies!!) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val layout = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        convertView = layout.inflate(R.layout.foodlist, parent, false)
        val r = getItem(position)
        val img = convertView.findViewById<ImageView>(R.id.imgRecipe)
        val txtName = convertView.findViewById<TextView>(R.id.rName)
        txtName.text = r!!.name
        println("IMG: " + r.img)
        if (!r.img.isEmpty()) {
            if (r.img.contains("http")) {
                DownloadImageTask(img).execute(r.img)
            } else {
                val imageBytes = Base64.decode(r.img, Base64.DEFAULT)
                val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                img.setImageBitmap(decodedImage)
            }
        } else {
            println("No Image Available")
        }
        convertView.setOnClickListener(View.OnClickListener { view(r.id) })
        return convertView
    }

    fun view(id: Int) {
        val intent = Intent(context, ViewRecipe::class.java)
        intent.putExtra("recipe_id", id)
        (context as RecipeList).startActivityForResult(intent, 202)
    }
}