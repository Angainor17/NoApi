package com.voronin.noapi

import android.content.IntentFilter
import android.net.wifi.p2p.WifiP2pManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.voronin.noapi.wifiP2P.WifiP2PHandler

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    private fun getIntentFilter(): IntentFilter {
        val intentFilter = IntentFilter()

        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)

        return intentFilter
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(WifiP2PHandler.wifiP2pManager?.receiver, getIntentFilter())
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(WifiP2PHandler.wifiP2pManager?.receiver)
    }
}
