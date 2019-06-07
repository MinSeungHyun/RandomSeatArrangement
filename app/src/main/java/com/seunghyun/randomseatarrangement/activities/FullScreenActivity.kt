package com.seunghyun.randomseatarrangement.activities

import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.seunghyun.randomseatarrangement.R
import com.seunghyun.randomseatarrangement.fragments.HomeFragment
import kotlinx.android.synthetic.main.activity_full_screen.*

class FullScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_screen)
        backButton.setOnClickListener { finish() }

        val bitmaps = HomeFragment.requestGridBitmap.getBitmaps()
        photoView.setImageBitmap(bitmaps[2])
    }

    companion object {
        fun convertViewToBitmap(view: View): Bitmap {
            val returnedBitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(returnedBitmap)
            view.draw(canvas)
            return returnedBitmap
        }
    }
}
