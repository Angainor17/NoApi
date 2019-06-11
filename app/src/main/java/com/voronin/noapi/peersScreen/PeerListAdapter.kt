package com.voronin.noapi.peersScreen

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.voronin.noapi.R
import com.voronin.noapi.peersScreen.models.PeerItem

class PeerListAdapter(private val clickListener: (PeerItem) -> Unit) :
    RecyclerView.Adapter<PeerListAdapter.ViewHolder>() {

    var list = ArrayList<PeerItem>()
        set(list) {
            field = list
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.peer_item, parent, false
            )
        )
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        holder.title.text = item.name
        holder.btn.setOnClickListener {
            clickListener.invoke(item)
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.title)
        val btn: Button = view.findViewById(R.id.button)
    }
}