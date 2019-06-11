package com.voronin.noapi.chatScreen

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.voronin.noapi.R
import com.voronin.noapi.chatScreen.models.User
import com.voronin.noapi.chatScreen.models.UserMessage
import java.text.SimpleDateFormat
import java.util.*


class MessageListAdapter(private val messageList: ArrayList<UserMessage>, private val currentUser: User) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemCount() = messageList.size

    override fun getItemViewType(position: Int): Int {
        val message = messageList[position]

        return if (message.sender == currentUser) {
            VIEW_TYPE_MESSAGE_SENT
        } else {
            VIEW_TYPE_MESSAGE_RECEIVED
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            SentMessageHolder(layoutInflater.inflate(R.layout.item_message_sent, parent, false))
        } else {
            ReceivedMessageHolder(layoutInflater.inflate(R.layout.item_message_received, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messageList[position]

        when (holder.itemViewType) {
            VIEW_TYPE_MESSAGE_SENT -> (holder as SentMessageHolder).bind(message)
            VIEW_TYPE_MESSAGE_RECEIVED -> (holder as ReceivedMessageHolder).bind(message)
        }
    }

    private class SentMessageHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var messageText: TextView = itemView.findViewById(R.id.text_message_body)
        var timeText: TextView = itemView.findViewById(R.id.text_message_time)

        fun bind(message: UserMessage) {
            messageText.text = message.message
            timeText.text = message.getDateString()
        }
    }

    private class ReceivedMessageHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var messageText: TextView = itemView.findViewById(R.id.text_message_body)
        var timeText: TextView = itemView.findViewById(R.id.text_message_time)
        var nameText: TextView = itemView.findViewById(R.id.text_message_name)

        fun bind(message: UserMessage) {
            messageText.text = message.message
            timeText.text = message.getDateString()
            nameText.text = message.sender.nickname
        }
    }

    companion object {
        private const val VIEW_TYPE_MESSAGE_SENT = 1
        private const val VIEW_TYPE_MESSAGE_RECEIVED = 2
    }

    fun update(list: ArrayList<UserMessage>) {
        messageList.clear()
        messageList.addAll(list)
        notifyDataSetChanged()
    }
}

fun UserMessage.getDateString(): String {
    val simpleDateFormat = SimpleDateFormat("HH:mm")
    return simpleDateFormat.format(Date(createdAt))
}