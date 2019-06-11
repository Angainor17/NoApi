package com.voronin.noapi.chatScreen

import android.app.Application
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.voronin.noapi.chatScreen.models.User
import com.voronin.noapi.chatScreen.models.UserMessage
import com.voronin.noapi.peersScreen.models.PeerItem
import com.voronin.noapi.utils.debug
import com.voronin.noapi.wifiP2P.WifiP2PHandler
import java.util.*


class ChatViewModel(application: Application) : AndroidViewModel(application) {

    val MESSAGE_READ = 1

    var currentUser: User = User("MEIZU", "123456")
    var userTo = User("NE MEIZU", "6666666")

    lateinit var peerItem: PeerItem
    val list = ArrayList<UserMessage>()

    private val messages: MutableLiveData<ArrayList<UserMessage>> = MutableLiveData()

    val handler = Handler(Handler.Callback {
        when (it.what) {
            MESSAGE_READ -> {
                val readBuff: ByteArray = it.obj as ByteArray
                val tempMsg = String(readBuff, 0, it.arg1)
                list.add(UserMessage(tempMsg, userTo, Date().time))
                messages.postValue(list)
                debug("RECEIVED = $tempMsg")
            }
        }
        true
    })

    init {
        WifiP2PHandler.wifiP2pManager?.handler = handler
    }

    fun initFromBundle(bundle: Bundle?) {
        peerItem = bundle?.getSerializable("peerItem") as PeerItem
        userTo = User(peerItem.name, peerItem.wifiP2pDevice.deviceAddress)
    }

    fun getMessages(): LiveData<ArrayList<UserMessage>> = messages

    fun sendText(text: String) {
        list.add(UserMessage(text, currentUser, Date().time))
        messages.postValue(list)
        AsyncTask.execute {
            WifiP2PHandler.wifiP2pManager?.sendMessage(text)
        }
    }

    fun connectTo() {
        WifiP2PHandler.wifiP2pManager?.connectTo(peerItem.wifiP2pDevice)
    }
}