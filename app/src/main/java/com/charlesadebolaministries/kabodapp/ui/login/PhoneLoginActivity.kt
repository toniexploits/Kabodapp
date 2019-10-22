package com.charlesadebolaministries.kabodapp.ui.login

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.charlesadebolaministries.kabodapp.R
import com.charlesadebolaministries.kabodapp.listeners.PhoneAuthListener
import com.google.firebase.auth.FirebaseAuth
import com.hbb20.CountryCodePicker
import kotlinx.android.synthetic.main.activity_phone_login.*

class PhoneLoginActivity : AppCompatActivity(), CountryCodePicker.OnCountryChangeListener, PhoneAuthListener {

    private var ccp : CountryCodePicker? = null
    private var countryCode : String? = null
    private var countryName : String? = null
    private var phoneNumber: String? = null

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_login)

        val phoneNoTxt = findViewById<EditText>(R.id.phoneNoET)
        val phoneAuthBtn = findViewById<Button>(R.id.phoneAuthBtn)

        ccp = findViewById(R.id.ccpCode)
        ccp!!.setOnCountryChangeListener(this)
        //to set default country code as Nigeria
        ccp!!.setDefaultCountryUsingNameCode("NG")

        onCountrySelected()

        phoneAuthBtn.setOnClickListener {
            val phone = phoneNoTxt.text.toString()

            if(validNo(phone)){
                val ph = phone.trimStart('0')
                phoneNumber = "+$countryCode " + phoneFormat(ph)
                onPhoneAuthClicked(countryName, phoneNumber)
            }

        }

    }

    fun verifyPhoneSendOTP(phone: String){

    }

    fun proceedToPhonseSignup(){

    }

    private fun phoneFormat(string: String) : String{
        var phoneNo = ""

        phoneNo = string.substring(0, 3) + " " + string.substring(3, 6) + " " + string.substring(6, 10)

        return phoneNo
    }

    override fun onCountrySelected() {
        countryCode = ccp!!.selectedCountryCode
        countryName = ccp!!.selectedCountryName
        //Toast.makeText(this,"Country is $countryName with Code $countryCode", Toast.LENGTH_SHORT).show()
    }

    override fun onPhoneAuthClicked(country: String?, phoneNumber: String?) {
//        println(country)
//        println(countryCode)
//        println(phoneNumber)
        startActivity(VerifyMobileActivity.newIntent(this@PhoneLoginActivity, phoneNumber, country))


    }

    private fun validNo(no : String) : Boolean{
        var proceed = true

//        if(no.isNullOrEmpty()){
//            phoneNoET.error = "Please enter the phone number!"
//            phoneNoET.requestFocus()
//            proceed = false
//        }

        if(no.isNullOrEmpty() || no.length < 10){
            phoneNoET.error = "Please enter valid number"
            phoneNoET.requestFocus()
            proceed = false
        }else if(no.length > 10 && no.first() == '0'){
            phoneNoET.error = "Invalid. More digits than possible."
            phoneNoET.requestFocus()
            proceed = false
        }


        return proceed
    }



    companion object{
        val PARAM_PHONE_NO = "phoneNumber"

        fun newIntent(context: Context) = Intent(context, PhoneLoginActivity::class.java)
    }
}

