package com.voronin.noapi.peersScreen.models

import android.net.wifi.p2p.WifiP2pDevice
import java.io.Serializable

class PeerItem(val name: String, val wifiP2pDevice: WifiP2pDevice) : Serializable