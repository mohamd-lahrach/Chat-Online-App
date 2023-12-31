package com.lahrachtech.messanger

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.lahrachtech.messanger.model.*
import com.lahrachtech.messanger.recyclerview.RecipientImageMessageItem
import com.lahrachtech.messanger.recyclerview.RecipientTextMessageItem
import com.lahrachtech.messanger.recyclerview.SenderImageMessageItem
import com.lahrachtech.messanger.recyclerview.SenderTextMessageItem
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_chat.*
import java.io.ByteArrayOutputStream
import java.util.*

class ChatActivity : AppCompatActivity() {

    private lateinit var mCurrentChatChannelId: String
    private val storageInstance: FirebaseStorage by lazy {
        FirebaseStorage.getInstance()
    }

    private val currentImageRef: StorageReference
        get() = storageInstance.reference

    private val firestoreInstance: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    private lateinit var mRecipientId: String

    private val currentUserDocRef: DocumentReference
        get() = firestoreInstance.document(
            "users/${FirebaseAuth.getInstance().currentUser?.uid}"
        )


    private val chatChannelsCollectionRef = firestoreInstance.collection("chatChannels")

    //Vars
    private lateinit var currentUser: User
    private var mCurrentUserId = FirebaseAuth.getInstance().currentUser!!.uid
    private val messageAdapter by lazy { GroupAdapter<GroupieViewHolder>() }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)


        getUserInfo { user ->
            currentUser = user
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } else {
            window.statusBarColor = Color.WHITE
        }


        val userName = intent.getStringExtra("user_name")
        val profileImage = intent.getStringExtra("profile_image")
        mRecipientId = intent.getStringExtra("other_uid").toString()

        textView_user_name.text = userName

//        fab_send_image.setOnClickListener {
//
//            val myIntentImage = Intent().apply {
//                type = "image/*"
//                action = Intent.ACTION_GET_CONTENT
//                putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png"))
//            }
//
//            startActivityForResult(Intent.createChooser(myIntentImage, "Select Image"), 2)
//        }


        createChatChannel { channelId ->

            mCurrentChatChannelId = channelId

            getMessages(channelId)

            imageView_send.setOnClickListener {

                if (editText_message.text.toString().isNotEmpty()) {

                    val messageSend =
                        TextMessage(
                            editText_message.text.toString(),
                            mCurrentUserId,
                            mRecipientId,
                            currentUser.name,
                            "",
                            Calendar.getInstance().time
                        )
                    sentMessage(channelId, messageSend, editText_message.text.toString())
                    editText_message.setText("")
                } else {
                    Toast.makeText(this, "Empty", Toast.LENGTH_LONG).show()
                }
            }
        }


        chat_recyclerView.apply {
            adapter = messageAdapter
        }

        if (profileImage!!.isNotEmpty()) {
            Glide.with(this@ChatActivity)
                .load(storageInstance.getReference(profileImage))
                .into(circleImageView_profile_picture)
        } else {
            circleImageView_profile_picture.setImageResource(R.drawable.user)
        }

        imageView_back.setOnClickListener {
            finish()
        }
    }

    private fun sentMessage(channelId: String, message: Message, text: String) {

        val contentMessage = mutableMapOf<String, Any>()
        contentMessage["text"] = text
        contentMessage["senderId"] = message.senderId
        contentMessage["recipientId"] = message.recipientId
        contentMessage["senderName"] = message.senderName
        contentMessage["recipientName"] = message.recipientName
        contentMessage["date"] = message.date
        contentMessage["type"] = message.type

        chatChannelsCollectionRef.document(channelId).collection("messages").add(message)

        firestoreInstance.collection("users")
            .document(FirebaseAuth.getInstance().currentUser!!.uid)
            .collection("sharedChat")
            .document(mRecipientId)
            .update(contentMessage)

        firestoreInstance.collection("users")
            .document(mRecipientId)
            .collection("sharedChat")
            .document(FirebaseAuth.getInstance().currentUser!!.uid)
            .update(contentMessage)
    }

    private fun createChatChannel(onComplete: (channelId: String) -> Unit) {

        firestoreInstance.collection("users")
            .document(FirebaseAuth.getInstance().currentUser!!.uid)
            .collection("sharedChat")
            .document(mRecipientId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    onComplete(document["channelId"] as String)
                    return@addOnSuccessListener
                }


                val newChatChannel = firestoreInstance.collection("users").document()


                firestoreInstance.collection("users")
                    .document(mRecipientId)
                    .collection("sharedChat")
                    .document(mCurrentUserId)
                    .set(mapOf("channelId" to newChatChannel.id))

                firestoreInstance.collection("users")
                    .document(mCurrentUserId)
                    .collection("sharedChat")
                    .document(mRecipientId)
                    .set(mapOf("channelId" to newChatChannel.id))

                onComplete(newChatChannel.id)
            }

    }

    private fun getMessages(channelId: String) {

        val query = chatChannelsCollectionRef.document(channelId).collection("messages")
            .orderBy("date", Query.Direction.DESCENDING)

        query.addSnapshotListener { querySnapshot, firebaseFirestoreException ->

            messageAdapter.clear()
            querySnapshot!!.documents.forEach { document ->


                if (document["type"] == MessageType.TEXT) {

                    val textMessage = document.toObject(TextMessage::class.java)
                    if (textMessage?.senderId == mCurrentUserId) {
                        messageAdapter.add(
                            SenderTextMessageItem(
                                document.toObject(TextMessage::class.java)!!,
                                document.id,
                                this@ChatActivity
                            )
                        )

                    } else {
                        messageAdapter.add(
                            RecipientTextMessageItem(
                                document.toObject(TextMessage::class.java)!!,
                                document.id,
                                this@ChatActivity
                            )
                        )
                    }
                } else {

                    val imageMessage = document.toObject(ImageMessage::class.java)
                    if (imageMessage?.senderId == mCurrentUserId) {
                        messageAdapter.add(
                            SenderImageMessageItem(
                                document.toObject(ImageMessage::class.java)!!,
                                document.id,
                                this@ChatActivity
                            )
                        )
                    } else {
                        messageAdapter.add(
                            RecipientImageMessageItem(
                                document.toObject(ImageMessage::class.java)!!,
                                document.id,
                                this@ChatActivity
                            )
                        )
                    }
                }
            }

        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 2 && resultCode == RESULT_OK && data != null && data.data != null) {


            val selectedImagePath = data.data
            val selectedImageBmp =
                MediaStore.Images.Media.getBitmap(this.contentResolver, selectedImagePath)
            val outputStream = ByteArrayOutputStream()
            selectedImageBmp.compress(Bitmap.CompressFormat.JPEG, 25, outputStream)

            val selectedImageBytes = outputStream.toByteArray()

            uploadImage(selectedImageBytes) { path ->
                val imageMessage = ImageMessage(
                    path,
                    mCurrentUserId,
                    mRecipientId,
                    currentUser.name,
                    "",
                    Calendar.getInstance().time
                )


                //chatChannelsCollectionRef.document(mCurrentChatChannelId).collection("messages").add(imageMessage)


                sentMessage(mCurrentChatChannelId, imageMessage, "PHOTO")
            }
        }

    }

    private fun uploadImage(selectedImageBytes: ByteArray, onSuccess: (imagePath: String) -> Unit) {

        val ref = currentImageRef.child(
            "${FirebaseAuth.getInstance().currentUser!!.uid}/images/${
                UUID.nameUUIDFromBytes(selectedImageBytes)
            }"
        )
        ref.putBytes(selectedImageBytes)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    onSuccess(ref.path)
                    Toast.makeText(this, "Done", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "error", Toast.LENGTH_LONG).show()
                }
            }

    }

    private fun getUserInfo(onComplete: (User) -> Unit) {
        currentUserDocRef.get().addOnSuccessListener {
            onComplete(it.toObject(User::class.java)!!)
        }
    }


}
