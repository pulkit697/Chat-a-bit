package com.example.chatapp.models

import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.example.chatapp.util.formatAsListItem
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.layout_rv_item.view.*

class InboxChatViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
    fun bind(inbox: Inbox,onClick:(name:String,image:String,uid:String) -> Unit){
        itemView.apply {
            tvUserNameRV.text = inbox.name
            tvMessageInRV.text = inbox.msg
            tvLastMessageTimingRV.text = inbox.time.formatAsListItem(context)
            tvUnreadMessagesBubbleRV.isVisible = inbox.count>0
            tvUnreadMessagesBubbleRV.text = inbox.count.toString()

            Picasso.get().load(inbox.image).error(R.drawable.default_avatar).into(ivUserImageRV)
            setOnClickListener {
                onClick.invoke(inbox.name,inbox.image,inbox.from)
            }
        }
    }
}