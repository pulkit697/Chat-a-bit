package com.example.chatapp.models

import com.google.firebase.firestore.FieldValue
import com.google.firestore.v1.DocumentTransform

data class User(
        var name:String,
        var imageUrl:String,
        var uid:String,
        var status:String,
        var deviceToken:String,
        var online:Boolean
)
{
    /* empty constructor necessary for firebase to work well */
    constructor() : this("","","","","", false)

    constructor(name: String,imageUrl: String,uid: String):
            this(name,
                    imageUrl,
                    uid,
                    "Hey there! I am using ChatEx",
                    "",
                    false
            )

}
