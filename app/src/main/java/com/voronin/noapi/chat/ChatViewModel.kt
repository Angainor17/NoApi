package com.voronin.noapi.chat

import android.app.Application
import android.os.Bundle
import androidx.lifecycle.AndroidViewModel
import com.voronin.noapi.peersScreen.PeerItem


class ChatViewModel(application: Application) : AndroidViewModel(application) {

    lateinit var peerItem: PeerItem

    var chatName: String = ""

    fun initFromBundle(bundle: Bundle?) {
        peerItem = bundle?.getSerializable("peerItem") as PeerItem
        chatName = "ChatWith ${peerItem.name}"
    }
}