package com.example.chatapp.models

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.layout_rv_item.view.*

class UserViewHolder(itemView:View):RecyclerView.ViewHolder(itemView) {
    fun bind(user:User,onClick:(name:String,image:String,uid:String)-> Unit) = with(itemView){
        tvUnreadMessagesBubbleRV.visibility = View.GONE
        tvLastMessageTimingRV.visibility = View.GONE

        tvUserNameRV.text = user.name
        tvMessageInRV.text = user.status

        Picasso.get()
                .load(user.imageUrl)
                .placeholder(R.drawable.default_avatar)
                .error(R.drawable.default_avatar)
                .into(ivUserImageRV)

        setOnClickListener {
            onClick.invoke(user.name,user.imageUrl,user.uid)
        }
    }
}