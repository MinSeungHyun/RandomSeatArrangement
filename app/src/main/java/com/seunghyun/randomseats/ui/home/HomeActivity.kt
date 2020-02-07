package com.seunghyun.randomseats.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.seunghyun.randomseats.R
import com.seunghyun.randomseats.databinding.ActivityHomeBinding
import com.seunghyun.randomseats.ui.BindingActivity
import com.seunghyun.randomseats.ui.arrangement.ArrangementActivity
import com.seunghyun.randomseats.ui.setting.SettingActivity

class HomeActivity : BindingActivity<ActivityHomeBinding>(R.layout.activity_home), HomeViewController {
    private val viewModel = HomeViewModel(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTitle(R.string.home)
        binding.apply {
            vm = viewModel
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_home, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.settings -> {
            startActivity(Intent(this, SettingActivity::class.java))
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun startArrangementActivity() = startActivity(Intent(this, ArrangementActivity::class.java))
}
