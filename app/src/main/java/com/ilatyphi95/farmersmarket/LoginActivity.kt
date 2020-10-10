package com.ilatyphi95.farmersmarket

import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputLayout

class LoginActivity : AppCompatActivity() {

    private lateinit var emailTextInputLayout: TextInputLayout
    private lateinit var passwordTextInputLayout: TextInputLayout
    private lateinit var signUpTextView: TextView

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
        setTheme(R.style.SplashTheme)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        emailTextInputLayout = findViewById(R.id.emailTextLayout)
        passwordTextInputLayout = findViewById(R.id.passwordLayout)
        signUpTextView = findViewById(R.id.signUpTextView)

        findViewById<Button>(R.id.loginButton).apply {
            setOnClickListener {
                confirmInputs()
            }
        }

        spanText()



    }

    private fun spanText(){
        val spannable = SpannableStringBuilder(signUpTextView.text.toString())

        val clickableSpan = object : ClickableSpan(){
            override fun onClick(widget: View) {

            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
                ds.color = ContextCompat.getColor(this@LoginActivity, R.color.green_shade_1)
                ds.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }
        }
        spannable.setSpan(clickableSpan,
            14,
            signUpTextView.text.toString().length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        signUpTextView.text = spannable
        signUpTextView.movementMethod = LinkMovementMethod.getInstance()

    }

    private fun validateEmail(): Boolean{
        val emptyEmailErrorMsg = "Email field cannot be empty"
        if (emailTextInputLayout.editText?.text.toString().trim().isEmpty()){
            emailTextInputLayout.error = emptyEmailErrorMsg
            return false
        }
        if(!emailTextInputLayout.editText?.text.toString().matches("[a-zA-z0-9.]+@[a-zA-Z-]+\\.(com|net|edu)".toRegex())){
            val invalidEmailMsg = "Email patter is invalid"
            emailTextInputLayout.error = invalidEmailMsg
            return false
        }
        emailTextInputLayout.error = null
        emailTextInputLayout.isErrorEnabled = false
        return true
    }

    private fun validatePassword(): Boolean{
        val emptyPasswordErrorMsg = "Password field cannot be empty"
        if (passwordTextInputLayout.editText?.text.toString().trim().isEmpty()){
            passwordTextInputLayout.error = emptyPasswordErrorMsg
            return false
        }
        passwordTextInputLayout.error = null
        passwordTextInputLayout.isErrorEnabled = false
        return true
    }

    private fun confirmInputs(){
        if (!validateEmail() || !validatePassword()){
            return
        }
        Toast.makeText(this, "You've logged in", Toast.LENGTH_LONG).show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent("com.action.killSplash")
        sendBroadcast(intent)
        finish()
    }
}