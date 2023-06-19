package com.lahrachtech.messanger.model

data class User(
    val name: String,
    val profileImage: String
) {
    constructor() : this("", "", )
}