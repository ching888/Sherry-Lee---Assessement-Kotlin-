package com.example.fourtitude_recipe_assessment.Background

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.util.Log
import android.widget.ImageView
import java.net.URL

class DownloadImageTask(var bmImage: ImageView) : AsyncTask<String?, Void?, Bitmap?>() {
    //@JvmName("doInBackground1")
    override fun doInBackground(vararg p0: String?): Bitmap? {
        val urldisplay = p0[0]
        var mIcon11: Bitmap? = null
        try {
            val `in` = URL(urldisplay).openStream()
            mIcon11 = BitmapFactory.decodeStream(`in`)
        } catch (e: Exception) {
            Log.e("Error", e.message!!)
            e.printStackTrace()
        }
        return mIcon11
    }

    override fun onPostExecute(result: Bitmap?) {
        bmImage.setImageBitmap(result)
    }
}