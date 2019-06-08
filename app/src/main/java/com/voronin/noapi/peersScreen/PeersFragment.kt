package com.voronin.noapi.peersScreen

import android.content.IntentFilter
import android.net.wifi.p2p.WifiP2pManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.voronin.noapi.R
import kotlinx.android.synthetic.main.peers_fragment_layout.*

class PeersFragment : Fragment() {

    private lateinit var viewModel: PeersViewModel

    private val intentFilter = IntentFilter()
    private val peerListAdapter = PeerListAdapter(this::openChat)

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(PeersViewModel::class.java)
        viewModel.getPeers().observe(this, Observer {
            peerListAdapter.list = it
        })

        initIntentFilter()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.peers_fragment_layout, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        navController = Navigation.findNavController(view)
    }

    override fun onResume() {
        super.onResume()
        activity?.registerReceiver(viewModel.getReceiver(), intentFilter)
    }

    override fun onPause() {
        super.onPause()
        activity?.unregisterReceiver(viewModel.getReceiver())
    }

    private fun initViews() {
        viewModel.initP2P()

        peersList.layoutManager = LinearLayoutManager(context)
        peersList.itemAnimator = DefaultItemAnimator()
        peersList.adapter = peerListAdapter

        discoverPeersBtn.setOnClickListener {
            viewModel.discoverPeers()
        }
    }

    private fun openChat(peerItem: PeerItem) {
        navController.navigate(R.id.action_peersFragment_to_chatFragment, bundleOf("peerItem" to peerItem))
    }

    private fun initIntentFilter() {
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)
    }
}