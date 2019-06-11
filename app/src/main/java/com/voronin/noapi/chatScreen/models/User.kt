package com.voronin.noapi.chatScreen.models

class User(var nickname: String, val id: String) {

    override fun equals(other: Any?): Boolean = other is User && other.id == id
}