package com.lahrachtech.messanger.recyclerview


import android.content.Context
import android.text.format.DateFormat
import com.lahrachtech.messanger.R
import com.lahrachtech.messanger.model.TextMessage
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.recipient_item_text_message.*

class RecipientTextMessageItem(
    private val textMessage: TextMessage,
    private val messageId: String,
    val context: Context
) : Item() {
    override fun bind(groupieViewHolder: GroupieViewHolder, position: Int) {

        groupieViewHolder.text_view_message.text = textMessage.text
        groupieViewHolder.text_view_time.text =
            DateFormat.format("hh:mm a", textMessage.date).toString()
    }

    override fun getLayout() = R.layout.recipient_item_text_message
}