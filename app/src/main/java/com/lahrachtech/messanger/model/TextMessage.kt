package com.lahrachtech.messanger.model

import java.util.*

data class TextMessage(
    val text: String,
    override val senderId: String,
    override val recipientId: String,
    override val senderName: String,
    override val recipientName: String,
    override val date: Date,
    override val type: String = MessageType.TEXT
) : Message {
    constructor() : this("", "", "","","", Date(), "")
}