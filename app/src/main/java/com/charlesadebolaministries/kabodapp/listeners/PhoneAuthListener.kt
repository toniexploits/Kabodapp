package com.charlesadebolaministries.kabodapp.listeners

interface PhoneAuthListener {
    fun onPhoneAuthClicked(country: String?, phoneNumber: String?)
}