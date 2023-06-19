package com.lahrachtech.messanger

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.lahrachtech.messanger.model.User
import kotlinx.android.synthetic.main.activity_signup.*

class SignupActivity : AppCompatActivity(), TextWatcher {

    private val mAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    private val firestoreInstance : FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }
    private val currentUserDocRef: DocumentReference
        get() = firestoreInstance.document("users/${mAuth.currentUser?.uid.toString()}")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O){
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }else{
            window.statusBarColor = Color.WHITE
        }
        etUsernameSignUp.addTextChangedListener(this@SignupActivity)
        etEmailSignUp.addTextChangedListener(this@SignupActivity)
        etPasswordSignUp.addTextChangedListener(this@SignupActivity)
        btnSignUp.setOnClickListener {
            val name = etUsernameSignUp.text.toString().trim()
            val email = etEmailSignUp.text.toString().trim()
            val password = etPasswordSignUp.text.toString().trim()
            val pattern = Regex("^[a-zA-Z0-9 ]{0,26}\$")
            if (!(pattern.matches(name))){
                etUsernameSignUp.requestFocus()
                TextInputLayoutUsernameSignUp.boxStrokeColor = Color.RED
                TextInputLayoutUsernameSignUp.helperText = "Name only contains letters and digits and 26 letters"
                TextInputLayoutUsernameSignUp.setHelperTextColor(ColorStateList.valueOf(Color.RED))
                TextInputLayoutUsernameSignUp.hintTextColor = ColorStateList.valueOf(Color.RED)
                return@setOnClickListener
            }else{
                TextInputLayoutUsernameSignUp.boxStrokeColor = resources.getColor(R.color.blue)
                TextInputLayoutUsernameSignUp.helperText = null
                TextInputLayoutUsernameSignUp.hintTextColor = ColorStateList.valueOf(resources.getColor(R.color.blue))
            }

            if (email.isEmpty()){
                etEmailSignUp.requestFocus()
                TextInputLayoutEmailSignUp.boxStrokeColor = Color.RED
                TextInputLayoutEmailSignUp.helperText = "Email Required"
                TextInputLayoutEmailSignUp.setHelperTextColor(ColorStateList.valueOf(Color.RED))
                TextInputLayoutEmailSignUp.hintTextColor = ColorStateList.valueOf(Color.RED)
                etEmailSignUp.requestFocus()
                return@setOnClickListener
            }else{
                TextInputLayoutEmailSignUp.boxStrokeColor = resources.getColor(R.color.blue)
                TextInputLayoutEmailSignUp.helperText = null
                TextInputLayoutEmailSignUp.hintTextColor = ColorStateList.valueOf(resources.getColor(R.color.blue))
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                etEmailSignUp.requestFocus()
                TextInputLayoutEmailSignUp.boxStrokeColor = Color.RED
                TextInputLayoutEmailSignUp.helperText = "Please enter a valid email"
                TextInputLayoutEmailSignUp.setHelperTextColor(ColorStateList.valueOf(Color.RED))
                TextInputLayoutEmailSignUp.hintTextColor = ColorStateList.valueOf(Color.RED)
                return@setOnClickListener
            }else{
                TextInputLayoutEmailSignUp.boxStrokeColor = resources.getColor(R.color.blue)
                TextInputLayoutEmailSignUp.helperText = null
                TextInputLayoutEmailSignUp.hintTextColor = ColorStateList.valueOf(resources.getColor(R.color.blue))
            }

            if (password.length < 6){
                etPasswordSignUp.requestFocus()
                TextInputLayoutPasswordSignUp.boxStrokeColor = Color.RED
                TextInputLayoutPasswordSignUp.helperText = "6 char required"
                TextInputLayoutPasswordSignUp.setHelperTextColor(ColorStateList.valueOf(Color.RED))
                TextInputLayoutPasswordSignUp.hintTextColor = ColorStateList.valueOf(Color.RED)
                return@setOnClickListener
            }else{
                TextInputLayoutPasswordSignUp.boxStrokeColor = resources.getColor(R.color.blue)
                TextInputLayoutPasswordSignUp.helperText = null
                TextInputLayoutPasswordSignUp.hintTextColor = ColorStateList.valueOf(resources.getColor(R.color.blue))
            }
            createNewAccount(name, email, password)
        }

    }

    private fun createNewAccount(name: String, email: String, password: String) {
        progressBarSignUp.visibility = View.VISIBLE
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {task ->

            val newUser = User(name, "")

            currentUserDocRef.set(newUser)

            if (task.isSuccessful){
                progressBarSignUp.visibility = View.INVISIBLE
                val intentMainActivity = Intent(this@SignupActivity, MainActivity::class.java)
                intentMainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intentMainActivity)
            } else {
                progressBarSignUp.visibility = View.INVISIBLE
                Toast.makeText(this@SignupActivity, task.exception?.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }


    override fun afterTextChanged(s: Editable?) {
        btnSignUp.isEnabled = etUsernameSignUp.text?.trim()!!.isNotEmpty()
                && etEmailSignUp.text?.trim()!!.isNotEmpty()
                && etPasswordSignUp.text?.trim()!!.isNotEmpty()

    }
}