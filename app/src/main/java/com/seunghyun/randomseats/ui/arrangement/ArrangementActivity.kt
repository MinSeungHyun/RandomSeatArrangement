package com.seunghyun.randomseats.ui.arrangement

import android.os.Bundle
import com.seunghyun.randomseats.R
import com.seunghyun.randomseats.databinding.ActivityArrangmentBinding
import com.seunghyun.randomseats.ui.BindingActivity

class ArrangementActivity : BindingActivity<ActivityArrangmentBinding>(R.layout.activity_arrangment) {
    private val viewModel by lazy { ArrangementViewModel() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTitle(R.string.arrangement)
        binding.apply {
            vm = viewModel
        }
    }
}