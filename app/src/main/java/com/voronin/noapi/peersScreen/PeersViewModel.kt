package com.voronin.noapi.peersScreen

import android.app.Application
import android.content.BroadcastReceiver
import android.net.wifi.p2p.WifiP2pManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.voronin.noapi.wifiP2P.WifiP2PManager


class PeersViewModel(application: Application) : AndroidViewModel(application) {

    private val peers: MutableLiveData<ArrayList<PeerItem>> = MutableLiveData()

    private val peerListListener = WifiP2pManager.PeerListListener { peerList ->
        val refreshedPeers = peerList.deviceList

        val newPeers = ArrayList<PeerItem>()
        refreshedPeers?.forEach {
            newPeers.add(PeerItem(it.deviceName, it))
        }
        peers.postValue(newPeers)

        return@PeerListListener
    }

    private val wifiP2PManager = WifiP2PManager(application, peerListListener)

    fun getPeers(): LiveData<ArrayList<PeerItem>> = peers

    fun getReceiver(): BroadcastReceiver = wifiP2PManager.receiver

    fun discoverPeers() {
        wifiP2PManager.discoverPeers()
    }

    fun initP2P() {
        wifiP2PManager.startRegistration()
        wifiP2PManager.discoverService()
    }
}