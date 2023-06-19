package com.lahrachtech.messanger

import android.app.ProgressDialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_signin.*
import kotlinx.android.synthetic.main.activity_signup.*

class SigninActivity : AppCompatActivity(), TextWatcher {
    private val mAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O){
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }else{
            window.statusBarColor = Color.WHITE
        }
        etEmailSignIn.addTextChangedListener(this@SigninActivity)
        etPasswordSignIn.addTextChangedListener(this@SigninActivity)
        btnSignIn.setOnClickListener {
            val email = etEmailSignIn.text.toString().trim()
            val password = etPasswordSignIn.text.toString().trim()
            if (email.isEmpty()){
                etEmailSignUp.requestFocus()
                TextInputLayoutEmailSignIn.boxStrokeColor = Color.RED
                TextInputLayoutEmailSignIn.helperText = "Email Required"
                TextInputLayoutEmailSignIn.setHelperTextColor(ColorStateList.valueOf(Color.RED))
                TextInputLayoutEmailSignIn.hintTextColor = ColorStateList.valueOf(Color.RED)
                etEmailSignUp.requestFocus()
                return@setOnClickListener
            }else{
                TextInputLayoutEmailSignIn.boxStrokeColor = resources.getColor(R.color.blue)
                TextInputLayoutEmailSignIn.helperText = null
                TextInputLayoutEmailSignIn.hintTextColor = ColorStateList.valueOf(resources.getColor(R.color.blue))
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                etEmailSignIn.requestFocus()
                TextInputLayoutEmailSignIn.boxStrokeColor = Color.RED
                TextInputLayoutEmailSignIn.helperText = "Please enter a valid email"
                TextInputLayoutEmailSignIn.setHelperTextColor(ColorStateList.valueOf(Color.RED))
                TextInputLayoutEmailSignIn.hintTextColor = ColorStateList.valueOf(Color.RED)
                return@setOnClickListener
            }else{
                TextInputLayoutEmailSignIn.boxStrokeColor = resources.getColor(R.color.blue)
                TextInputLayoutEmailSignIn.helperText = null
                TextInputLayoutEmailSignIn.hintTextColor = ColorStateList.valueOf(resources.getColor(R.color.blue))
            }

            if (password.length < 6){
                etPasswordSignIn.requestFocus()
                TextInputLayoutPasswordSignIn.boxStrokeColor = Color.RED
                TextInputLayoutPasswordSignIn.helperText = "6 char required"
                TextInputLayoutPasswordSignIn.setHelperTextColor(ColorStateList.valueOf(Color.RED))
                TextInputLayoutPasswordSignIn.hintTextColor = ColorStateList.valueOf(Color.RED)
                return@setOnClickListener
            }else{
                TextInputLayoutPasswordSignIn.boxStrokeColor = resources.getColor(R.color.blue)
                TextInputLayoutPasswordSignIn.helperText = null
                TextInputLayoutPasswordSignIn.hintTextColor = ColorStateList.valueOf(resources.getColor(R.color.blue))
            }
            signIn(email, password)
        }
        btnCreateAccount.setOnClickListener {
            val intentToSignup = Intent(this@SigninActivity, SignupActivity::class.java)
            startActivity(intentToSignup)
        }
    }

    override fun onStart() {
        super.onStart()
        if (mAuth.currentUser?.uid != null){
            val intentMainActivity = Intent(this@SigninActivity, MainActivity::class.java)
            startActivity(intentMainActivity)
        }
    }

    private fun signIn(email: String, password: String) {
        val progressDialog = ProgressDialog(this@SigninActivity)
        progressDialog.setMessage("Checking user authentication...")
        progressDialog.setCancelable(false)
        progressDialog.show()
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful){
                progressDialog.dismiss()
                val intentMainActivity = Intent(this@SigninActivity, MainActivity::class.java)
                intentMainActivity.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intentMainActivity)
            }else{
                progressDialog.dismiss()
                Toast.makeText(this@SigninActivity, task.exception?.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    override fun afterTextChanged(s: Editable?) {
        btnSignIn.isEnabled = etEmailSignIn.text.toString().trim().isNotEmpty()
                && etPasswordSignIn.text.toString().trim().isNotEmpty()
    }
}