package com.voronin.noapi.chatScreen

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.voronin.noapi.utils.debug
import kotlinx.android.synthetic.main.chat_fragment_layout.*


class ChatFragment : Fragment() {

    private lateinit var viewModel: ChatViewModel

    private lateinit var messageAdapter: MessageListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ChatViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(com.voronin.noapi.R.layout.chat_fragment_layout, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() {
        viewModel.initFromBundle(arguments)
        messageAdapter = MessageListAdapter(ArrayList(), viewModel.currentUser)

        chatList.layoutManager = LinearLayoutManager(context)
        chatList.itemAnimator = DefaultItemAnimator()
        chatList.adapter = messageAdapter

        viewModel.getMessages().observe(this, Observer {
            messageAdapter.update(it)
        })
        viewModel.connectTo()

        sendMessage.setOnClickListener {
            val text = messageEditText.text.toString()
            if (!text.isEmpty()) {
                viewModel.sendText(text)
                messageEditText.setText("")
            }
        }
    }
}