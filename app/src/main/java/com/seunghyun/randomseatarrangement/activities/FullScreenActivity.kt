package com.seunghyun.randomseatarrangement.activities

import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.seunghyun.randomseatarrangement.R
import com.seunghyun.randomseatarrangement.fragments.HomeFragment
import kotlinx.android.synthetic.main.activity_full_screen.*

class FullScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_full_screen)
        backButton.setOnClickListener { finish() }
        photoView.setOnClickListener {
            if (virtualActionBar.visibility == View.VISIBLE) {
                virtualActionBar.visibility = View.GONE
                backButton.visibility = View.GONE
            } else {
                virtualActionBar.visibility = View.VISIBLE
                backButton.visibility = View.VISIBLE
            }
        }

        val bitmaps = HomeFragment.requestGridBitmap.getBitmaps()
        if (bitmaps.size == 1) photoView.setImageBitmap(bitmaps[0])
        else {
            val bitmap = Bitmap.createBitmap(bitmaps[2].width + bitmaps[0].width, bitmaps[1].height + bitmaps[0].height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            canvas.drawBitmap(bitmaps[1], bitmaps[2].width.toFloat(), 0f, null)
            canvas.drawBitmap(bitmaps[2], 0f, bitmaps[1].height.toFloat(), null)
            canvas.drawBitmap(bitmaps[0], bitmaps[2].width.toFloat(), bitmaps[1].height.toFloat(), null)
            photoView.setImageBitmap(bitmap)
        }
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
