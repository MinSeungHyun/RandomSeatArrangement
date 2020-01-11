package com.seunghyun.randomseats.ui.home

class HomeViewModel(private val controller: HomeViewController) {
    fun onCreateButtonClick() {
        controller.startArrangementActivity()
    }
}