package com.example.chatapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.example.chatapp.models.ChatEvent
import com.example.chatapp.models.DateHeader
import com.example.chatapp.models.EmptyViewHolder
import com.example.chatapp.models.Message
import com.example.chatapp.util.formatAsListItem
import com.example.chatapp.util.formatAsTime
import kotlinx.android.synthetic.main.layout_date_header.view.*
import kotlinx.android.synthetic.main.layout_message_sent.view.*
import kotlinx.android.synthetic.main.layout_received_message.view.*

class ChatAdapter(private val list:MutableList<ChatEvent>,private val myId:String):RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = { layout:Int ->
            LayoutInflater.from(parent.context).inflate(layout,parent,false)
        }
        return when(viewType){
            MESSAGE_RECEIVED -> MessageViewHolder(inflater(R.layout.layout_received_message))
            MESSAGE_SENT -> MessageViewHolder(inflater(R.layout.layout_message_sent))
            DATE_HEADER -> DateViewHolder(inflater(R.layout.layout_date_header))
            else -> EmptyViewHolder(inflater(R.layout.layout_empty))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(getItemViewType(position))
        {
            DATE_HEADER -> (holder as DateViewHolder).bind(list[position] as DateHeader)
            MESSAGE_SENT -> (holder as MessageViewHolder).bindSentMessage(list[position] as Message)
            MESSAGE_RECEIVED -> (holder as MessageViewHolder).bindReceivedMessage(list[position] as Message)
            else -> (holder as EmptyViewHolder)
        }
    }

    override fun getItemCount(): Int = list.size

    override fun getItemViewType(position: Int): Int = when(val event  = list[position])
        {
            is Message -> {
                if(event.senderId == myId)
                    MESSAGE_SENT
                else
                    MESSAGE_RECEIVED
            }
            is DateHeader -> DATE_HEADER
            else -> UNSUPPORTED
        }

    inner class DateViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
        fun bind(dateHeader:DateHeader){
            itemView.apply {
                tvDateHeader.text = dateHeader.date
            }
        }
    }

    inner class MessageViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
        fun bindSentMessage(message:Message){
            itemView.apply {
                tvSentMessage.text = message.msg
                tvSentMessageTime.text = message.sentAt.formatAsTime()
            }
        }
        fun bindReceivedMessage(message: Message){
            itemView.apply {
                tvReceivedMessage.text = message.msg
                tvReceivedMessageTime.text = message.sentAt.formatAsTime()
            }
        }
    }


    companion object{
        private const val UNSUPPORTED=-1
        private const val MESSAGE_SENT = 0
        private const val MESSAGE_RECEIVED = 1
        private const val DATE_HEADER = 2
    }

}