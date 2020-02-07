package com.seunghyun.randomseats.ui.setting

import android.os.Bundle
import com.seunghyun.randomseats.R
import com.seunghyun.randomseats.databinding.ActivitySettingBinding
import com.seunghyun.randomseats.ui.BindingActivity

class SettingActivity : BindingActivity<ActivitySettingBinding>(R.layout.activity_setting) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTitle(R.string.settings)
    }
}