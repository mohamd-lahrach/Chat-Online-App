package com.lahrachtech.messanger.recyclerview

import android.content.Context
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.lahrachtech.messanger.R
import com.lahrachtech.messanger.model.User
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.recycler_view_item.*
import java.util.*


class ChatItem(
    val uid: String,
    val user: User,
//    val textMessage: TextMessage,
    val context: Context
) : Item() {
    private val storageInstance: FirebaseStorage by lazy {
        FirebaseStorage.getInstance()
    }
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.item_name_textView.text = user.name
        viewHolder.item_last_message_textView.text = "Last Message..."
        viewHolder.item_tiem_textView.text = "10:21"

        if (user.profileImage.isNotEmpty()){
            Glide.with(context)
                .load(storageInstance.getReference(user.profileImage))
                .into(viewHolder.item_circleImageView)
        }else{
            viewHolder.item_circleImageView.setImageResource(R.drawable.user)
        }
    }

    override fun getLayout(): Int = R.layout.recycler_view_item
}