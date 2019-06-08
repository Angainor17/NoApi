package com.voronin.noapi.wifiP2P

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.p2p.WifiP2pManager
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo
import android.os.Looper
import com.voronin.noapi.utils.debug

class WifiP2PManager(application: Application, val peerListListener: WifiP2pManager.PeerListListener) {

    companion object {
        private const val SERVER_PORT = "8888"
    }

    val receiver: BroadcastReceiver

    private var manager: WifiP2pManager = application.getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
    private val channel: WifiP2pManager.Channel

    private val buddies = mutableMapOf<String, String>()

    init {
        channel = manager.initialize(application, Looper.getMainLooper(), null)

        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                when (intent.action) {
                    WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> {
                        manager.requestPeers(channel, peerListListener)
                        debug("P2P peers changed")
                    }
                }
            }
        }
    }

    fun discoverPeers() {
        manager.discoverPeers(channel, object : WifiP2pManager.ActionListener {

            override fun onSuccess() {
                debug("discoverPeers onSuccess")
            }

            override fun onFailure(reasonCode: Int) {
                debug("discoverPeers onFailure $reasonCode")
            }
        })
    }

    fun discoverService() {
        val txtListener = WifiP2pManager.DnsSdTxtRecordListener { fullDomain, record, device ->
            debug("DnsSdTxtRecord available -$record")
            record["buddyname"]?.also {
                buddies[device.deviceAddress] = it
            }
        }

        val servListener = WifiP2pManager.DnsSdServiceResponseListener { instanceName, registrationType, resourceType ->
            resourceType.deviceName = buddies[resourceType.deviceAddress] ?: resourceType.deviceName
        }

        manager.setDnsSdResponseListeners(channel, servListener, txtListener)
    }

    fun startRegistration() {
        //  Create a string map containing information about your service.
        val record: Map<String, String> = mapOf(
            "listenport" to SERVER_PORT,
            "buddyname" to "John Doe${(Math.random() * 1000).toInt()}",
            "available" to "visible"
        )

        val serviceInfo = WifiP2pDnsSdServiceInfo.newInstance("_test", "_presence._tcp", record)

        manager.addLocalService(channel, serviceInfo, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                debug("LocalService onSuccess")
            }

            override fun onFailure(arg0: Int) {
                debug("LocalService onFailure ${(arg0)}")
            }
        })
    }
}