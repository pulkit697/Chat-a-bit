package com.example.chatapp

import com.google.firebase.firestore.FieldValue

data class User(
        var name:String,
        var imageUrl:String,
        var uid:String,
        var status:String,
        var deviceToken:String,
        var lastSeen: FieldValue
)
{
    /* empty constructor necessary for firebase to work well */
    constructor() : this("","","","","", FieldValue.serverTimestamp())

    constructor(name: String,imageUrl: String,uid: String):
            this(name,
                    imageUrl,
                    uid,
                    "Hey there! I am using ChatEx",
                    "",
                    FieldValue.serverTimestamp()
            )

}
