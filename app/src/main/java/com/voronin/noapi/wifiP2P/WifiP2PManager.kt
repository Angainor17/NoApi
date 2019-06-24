package com.voronin.noapi.wifiP2P

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.NetworkInfo
import android.net.wifi.WpsInfo
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pManager
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo
import android.os.Handler
import android.os.Looper
import com.voronin.noapi.utils.debug
import java.io.InputStream
import java.io.OutputStream
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket

class WifiP2PManager(application: Application, val peerListListener: WifiP2pManager.PeerListListener) {

    companion object {
        private const val SERVER_PORT = "8080"
    }

    val receiver: BroadcastReceiver

    private var manager: WifiP2pManager? = application.getSystemService(Context.WIFI_P2P_SERVICE) as? WifiP2pManager
    private val channel: WifiP2pManager.Channel?

    private val buddies = mutableMapOf<String, String>()

    lateinit var serverClass: ServerClass
    lateinit var clientClass: ClientClass

    var sendReceive: SendReceive? = null

    init {
        channel = manager?.initialize(application, Looper.getMainLooper(), null)

        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                when (intent.action) {
                    WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {
                        debug("WIFI_P2P_CONNECTION_CHANGED_ACTION !!")

                        manager.let { manager ->

                            val networkInfo: NetworkInfo? = intent
                                .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO) as NetworkInfo

                            if (networkInfo?.isConnected == true) {
                                manager?.requestConnectionInfo(channel, connectionInfoListener)
                                manager?.requestConnectionInfo(channel) {

                                    debug("groupOwnerAddress = " + it.groupOwnerAddress)
                                }
                            } else {
                                debug("Nani???")
                            }
                        }
                    }
                    WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> {
                        manager?.requestPeers(channel, peerListListener)
                        debug("P2P peers changed")
                    }
                }
            }
        }
    }

    val connectionInfoListener = WifiP2pManager.ConnectionInfoListener {
        val groupOwnerAddress: InetAddress? = it.groupOwnerAddress

        if (it.groupFormed && it.isGroupOwner) {
            //Host
            serverClass = ServerClass()
            serverClass.start()

        } else if (it.groupFormed) {
            //Client
            groupOwnerAddress.let {
                clientClass = ClientClass(it!!)
                clientClass.start()
            }

        }
    }

    fun sendMessage(message: String) {
        sendReceive?.write(message.toByteArray())
    }

    val MESSAGE_READ = 1

    var handler: Handler? = null

    fun connectTo(wifiP2pDevice: WifiP2pDevice) {
        val config = WifiP2pConfig().apply {
            deviceAddress = wifiP2pDevice.deviceAddress
            wps.setup = WpsInfo.PBC
        }

        manager?.connect(channel, config, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                debug("success connect to deviceName = " + wifiP2pDevice.deviceName)
                debug("success connect to " + wifiP2pDevice.deviceAddress)
                debug("" + config.deviceAddress)
                debug("" + config.wps)
            }

            override fun onFailure(reason: Int) {
                debug("onFailure connect to (reason) " + wifiP2pDevice.deviceName)
            }
        })
        manager?.requestConnectionInfo(channel, connectionInfoListener)
    }

    fun discoverPeers() {
        manager?.discoverPeers(channel, object : WifiP2pManager.ActionListener {

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

        val servListener = WifiP2pManager.DnsSdServiceResponseListener { _, _, resourceType ->
            resourceType.deviceName = buddies[resourceType.deviceAddress] ?: resourceType.deviceName
        }

        manager?.setDnsSdResponseListeners(channel, servListener, txtListener)
    }

    fun startRegistration() {
        val record: Map<String, String> = mapOf(
            "listenport" to SERVER_PORT,
            "buddyname" to "John Doe${(Math.random() * 1000).toInt()}",
            "available" to "visible"
        )

        val serviceInfo = WifiP2pDnsSdServiceInfo.newInstance("_test", "_presence._tcp", record)

        manager?.addLocalService(channel, serviceInfo, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                debug("LocalService onSuccess")
            }

            override fun onFailure(arg0: Int) {
                debug("LocalService onFailure ${(arg0)}")
            }
        })
    }

    inner class SendReceive(
        val socket: Socket,
        val inputStream: InputStream = socket.getInputStream(),
        val outputStream: OutputStream = socket.getOutputStream()
    ) : Thread() {
        override fun run() {
            val buffer = ByteArray(1024)
            var bytes: Int

            while (socket != null) {
                try {
                    bytes = inputStream.read(buffer)
                    debug("SendReceive ")
                    if (bytes > 0) {
                        handler?.obtainMessage(MESSAGE_READ, bytes, -1, buffer)?.sendToTarget()
                    }
                } catch (e: Exception) {

                }
            }
        }

        fun write(bytes: ByteArray) {
            try {
                outputStream.write(bytes)
            } catch (e: Exception) {

            }
        }
    }

    inner class ClientClass(inetAddress: InetAddress) : Thread() {

        val socket: Socket = Socket()
        val hostAdd: String = inetAddress.hostAddress

        override fun run() {
            super.run()
            try {
                socket.connect(InetSocketAddress(hostAdd, 8080), 100500)
                sendReceive = SendReceive(socket)
                sendReceive!!.start()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    inner class ServerClass : Thread() {
        lateinit var socket: Socket
        lateinit var serverSocket: ServerSocket

        override fun run() {
            try {
                serverSocket = ServerSocket(8080)
                socket = serverSocket.accept()
                sendReceive = SendReceive(socket)
                sendReceive!!.start()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}