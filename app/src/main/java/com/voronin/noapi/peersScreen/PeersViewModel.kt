package com.voronin.noapi.peersScreen

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.voronin.noapi.peersScreen.models.PeerItem


class PeersViewModel(application: Application) : AndroidViewModel(application) {

    private val peers: MutableLiveData<ArrayList<PeerItem>> = MutableLiveData()


    fun getPeers(): LiveData<ArrayList<PeerItem>> = peers

}