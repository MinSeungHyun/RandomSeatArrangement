package com.seunghyun.randomseats.activities

import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.seunghyun.randomseats.R
import com.seunghyun.randomseats.fragments.HomeFragment
import com.seunghyun.randomseats.utils.DataViewModel
import kotlinx.android.synthetic.main.activity_full_screen.*


class FullScreenActivity : AppCompatActivity() {
    private val model: DataViewModel by lazy {
        ViewModelProviders.of(this@FullScreenActivity).get(DataViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_full_screen)

        backButton.setOnClickListener { finish() }

        photoView.setOnClickListener {
            if (virtualActionBar.visibility == View.VISIBLE) {
                virtualActionBar.visibility = View.GONE
                backButton.visibility = View.GONE
//                saveButton.visibility = View.GONE
            } else {
                virtualActionBar.visibility = View.VISIBLE
                backButton.visibility = View.VISIBLE
//                saveButton.visibility = View.VISIBLE
            }
        }

        val image: Bitmap?
        if (model.seatImage.value == null) {
            val bitmaps = HomeFragment.requestGridBitmap.getBitmaps()
            image = mergeBitmaps(bitmaps)
            model.seatImage.value = image
        } else {
            image = model.seatImage.value
        }
        photoView.setImageBitmap(image)

//        saveButton.setOnClickListener {
//            //저장하기
//            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
//                ActivityCompat.requestPermissions(this@FullScreenActivity, Array(1) { Manifest.permission.WRITE_EXTERNAL_STORAGE }, 1)
//            } else {
//                saveBitmapToPath(bitmap, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString(), "RandomSeatArrangement.png", applicationContext)
//            }
//
//            //공유하기
//            val shareIntent: Intent = Intent().apply {
//                action = Intent.ACTION_SEND
//                putExtra(Intent.EXTRA_STREAM, getResizedBitmap(bitmap, 1, 1))
//                type = "image/jpeg"
//            }
//            startActivity(Intent.createChooser(shareIntent, resources.getText(R.string.share_to)))
//        }
    }

//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//
//        if (grantResults[0] == 0) {
//            //permission granted
//            saveBitmapToPath(bitmap, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString(), "RandomSeatArrangement.png", applicationContext)
//        } else {
//            Toast.makeText(applicationContext, "failed to save", Toast.LENGTH_LONG).show()
//        }
//    }

    companion object {
        fun convertViewToBitmap(view: View): Bitmap {
            val returnedBitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(returnedBitmap)
            view.draw(canvas)
            return returnedBitmap
        }

        fun mergeBitmaps(bitmaps: ArrayList<Bitmap>): Bitmap {
            val bitmap: Bitmap
            if (bitmaps.size == 1) bitmap = bitmaps[0]
            else {
                bitmap = Bitmap.createBitmap(bitmaps[2].width + bitmaps[0].width, bitmaps[1].height + bitmaps[0].height, Bitmap.Config.ARGB_8888)
                with(Canvas(bitmap)) {
                    drawBitmap(bitmaps[1], bitmaps[2].width.toFloat(), 0f, null)
                    drawBitmap(bitmaps[2], 0f, bitmaps[1].height.toFloat(), null)
                    drawBitmap(bitmaps[0], bitmaps[2].width.toFloat(), bitmaps[1].height.toFloat(), null)
                }
            }
            return bitmap
        }

//        fun getResizedBitmap(image: Bitmap, bitmapWidth: Int, bitmapHeight: Int): Bitmap {
//            return Bitmap.createScaledBitmap(image, bitmapWidth, bitmapHeight, true)
//        }
//
//        fun saveBitmapToPath(bitmap: Bitmap, filePath: String, fileName: String, context: Context) {
//            val file = File(filePath)
//            if (!file.exists()) file.mkdirs()
//
//            val fileCacheItem = File("$filePath/$fileName")
//            fileCacheItem.createNewFile()
//            bitmap.compress(Bitmap.CompressFormat.PNG, 100, FileOutputStream(fileCacheItem))
//            Toast.makeText(context, fileCacheItem.toString(), Toast.LENGTH_LONG).show()
//        }
    }
}
