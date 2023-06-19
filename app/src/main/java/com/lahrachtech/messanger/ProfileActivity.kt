package com.lahrachtech.messanger

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.lahrachtech.messanger.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_profile.*
import java.io.ByteArrayOutputStream

class ProfileActivity : AppCompatActivity() {

    companion object {
        const val RC_SELECT_IMAGE = 2
    }

    private lateinit var userName: String

    private val firestoreInstance: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    private val currentUserDocRef: DocumentReference
        get() = firestoreInstance.document("users/${FirebaseAuth.getInstance().currentUser?.uid.toString()}")

    private val storageInstance: FirebaseStorage by lazy {
        FirebaseStorage.getInstance()
    }

    private val currentUserStorageRef: StorageReference
        get() = storageInstance.reference.child(FirebaseAuth.getInstance().currentUser?.uid.toString())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } else {
            window.statusBarColor = Color.WHITE
        }

        btnSignOutProfile.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this@ProfileActivity, SigninActivity::class.java))
            finish()
        }

        setSupportActionBar(profileToolbar)
        supportActionBar?.title = "Me"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        getUserInfo { user ->
            userName = user.name

            tvUsernameProfile.text = user.name
            if (user.profileImage.isNotEmpty()){
                Glide.with(this@ProfileActivity)
                    .load(storageInstance.getReference(user.profileImage))
                    .placeholder(R.drawable.user)
                    .into(ivImageProfile)
            }

        }

        ivImageProfile.setOnClickListener {
            val myIntentImage = Intent().apply {
                type = "image/*"
                action = Intent.ACTION_GET_CONTENT
                putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png"))
            }
            startActivityForResult(Intent.createChooser(myIntentImage, "Select Image"), RC_SELECT_IMAGE)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SELECT_IMAGE && resultCode == RESULT_OK &&
            data != null && data.data != null) {

            progressBarProfile.visibility = View.VISIBLE

            val selectedImagePath = data.data
            val selectedImageBmp = MediaStore.Images.Media.getBitmap(this.contentResolver, selectedImagePath)
            val outputStream = ByteArrayOutputStream()
            selectedImageBmp.compress(Bitmap.CompressFormat.JPEG, 20, outputStream)
            val selectedImageBytes = outputStream.toByteArray()

            ivImageProfile.setImageURI(data.data)

            uploadProfileImage(selectedImageBytes) { path ->
                val userFieldMap = mutableMapOf<String, Any>()
                userFieldMap["name"] = userName
                userFieldMap["profileImage"] = path
                currentUserDocRef.update(userFieldMap)
            }


        }
    }

    private fun uploadProfileImage(selectedImageBytes: ByteArray, onSuccess: (imagePath: String) -> Unit) {

        val ref = currentUserStorageRef.child("profilePictures/profileImage")
        ref.putBytes(selectedImageBytes).addOnCompleteListener {
            if (it.isSuccessful) {
                onSuccess(ref.path)
                progressBarProfile.visibility = View.GONE
                Toast.makeText(this@ProfileActivity, "Your Profile added with success.", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this@ProfileActivity, "Error : ${it.exception?.message.toString()}", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return false
    }

    private fun getUserInfo(onComplete:(User) -> Unit){
        currentUserDocRef.get().addOnSuccessListener {
            onComplete(it.toObject(User::class.java)!!)
        }
    }
}