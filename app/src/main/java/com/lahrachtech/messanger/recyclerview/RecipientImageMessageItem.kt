package com.lahrachtech.messanger.recyclerview

import android.content.Context
import android.text.format.DateFormat
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.lahrachtech.messanger.R
import com.lahrachtech.messanger.model.ImageMessage
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.sender_item_image_message.*

class RecipientImageMessageItem(
    private val imageMessage: ImageMessage,
    private val messageId: String,
    val context: Context
) : Item() {

    private val storageInstance: FirebaseStorage by lazy {
        FirebaseStorage.getInstance()
    }

    override fun bind(groupieViewHolder: GroupieViewHolder, position: Int) {

        groupieViewHolder.textView_message_time.text =
            DateFormat.format("hh:mm a", imageMessage.date).toString()

        if (imageMessage.imagePath.isNotEmpty()) {
            Glide.with(context)
                .load(storageInstance.getReference(imageMessage.imagePath))
                .placeholder(R.drawable.ic_image_black_24dp)
                .into(groupieViewHolder.imageView_message_image)
        }
    }

    override fun getLayout() = R.layout.recipient_item_image_message
}