package com.seunghyun.randomseats.ui.home

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.seunghyun.randomseats.R
import com.seunghyun.randomseats.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {
    private val viewModel = HomeViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTitle(R.string.home)
        DataBindingUtil.setContentView<ActivityHomeBinding>(this, R.layout.activity_home).apply {
            vm = viewModel
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_home, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.settings -> true
        else -> super.onOptionsItemSelected(item)
    }
}
