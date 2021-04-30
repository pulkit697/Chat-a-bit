package com.example.chatapp.models

import android.content.Context
import com.example.chatapp.util.formatAsHeader
import java.util.*

interface ChatEvent{
    val sentAt:Date
}

data class Message(
        val msg:String,
        val msgId:String,
        val senderId:String,
        val type:String,
        val status:Int,
        override val sentAt: Date
) :ChatEvent{
    constructor():this("","","","TEXT",1,Date())
    constructor(msg:String,msgId: String,senderId: String) : this(msg,msgId,senderId,"TEXT",1,Date())
}

data class DateHeader(
        override val sentAt: Date,
        val context:Context
        ):ChatEvent{
    val date:String = sentAt.formatAsHeader(context)
}