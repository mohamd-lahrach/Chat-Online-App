package com.lahrachtech.messanger

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.lahrachtech.messanger.fragmets.ChatFragment
import com.lahrachtech.messanger.fragmets.DiscoverFragment
import com.lahrachtech.messanger.fragmets.PeopleFragment
import com.lahrachtech.messanger.model.User
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    private val mChatFragment = ChatFragment()
    private val mPeopleFragment = PeopleFragment()
    private val mMoreFragment = DiscoverFragment()

    private val firestoreInstance: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    private val storageInstance: FirebaseStorage by lazy {
        FirebaseStorage.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        firestoreInstance.collection("users")
            .document(FirebaseAuth.getInstance().currentUser?.uid.toString())
            .get()
            .addOnSuccessListener {
                val user = it.toObject(User::class.java)

                if (user!!.profileImage.isNotEmpty()) {
                    Glide.with(this@MainActivity)
                        .load(storageInstance.getReference(user.profileImage))
                        .placeholder(R.drawable.user)
                        .into(circleImageView_profile_image)
                } else {
                    circleImageView_profile_image.setImageResource(R.drawable.user)
                }
            }


        setSupportActionBar(toolbar_main)
        supportActionBar?.title = ""

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } else {
            window.statusBarColor = Color.WHITE
        }
        bottomNavigationView_main.setOnNavigationItemSelectedListener(this@MainActivity)

        setFragment(mChatFragment,R.id.coordinatorLayout_main_content)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.navigation_chat_item -> {
                setFragment(mChatFragment,R.id.coordinatorLayout_main_content)
                return true
            }

            R.id.navigation_people_item -> {
                setFragment(mPeopleFragment,R.id.coordinatorLayout_main_content)
                return true
            }

            R.id.navigation_more_item -> {
                setFragment(mMoreFragment,R.id.coordinatorLayout_main_content)
                return true
            }

            else -> return false
        }
    }

    private fun setFragment(fragment: Fragment, layout_file: Int) {
        val fr = supportFragmentManager.beginTransaction()
        fr.replace(layout_file, fragment)
        fr.commit()
    }
}
