package com.charlesadebolaministries.kabodapp.util

import android.content.Context
import android.widget.ImageView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.charlesadebolaministries.kabodapp.R
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

fun populateImage(context: Context?, url: String?, imageView: ImageView, errorDrawable: Int = R.drawable.empty){
    if(context != null) {
        val options = RequestOptions()
            .placeholder(progressDrawable(context))
            .error(errorDrawable)

        Glide.with(context)
            .load(url)
            .apply(options)
            .into(imageView)
    }
}

fun progressDrawable(context: Context): CircularProgressDrawable {
    return CircularProgressDrawable(context).apply {
        strokeWidth = 10f
        centerRadius = 40f
        start()
    }
}

fun getTime(): String{
//    val df = DateFormat.getInstance()
    val df = SimpleDateFormat("dd/MM/yyyy hh:mm a")
    return df.format(Date())
}