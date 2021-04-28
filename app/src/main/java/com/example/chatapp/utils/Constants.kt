package com.example.chatapp.utils

import android.app.ProgressDialog
import android.content.Context

const val PHONE_NUMBER_REQUEST_CODE = 11923
const val READ_WRITE_PERMISSION_REQUEST_CODE = 12543
const val KEY_PHONE_NUMBER = "phoneNumber"
const val IMAGE_FROM_GALLERY_REQUEST_CODE = 34556
const val DEFAULT_AVATAR_URL = "https://firebasestorage.googleapis.com/v0/b/chat-a-bit-9374e.appspot.com/o/uploads%2Fdefault_avatar.png?alt=media&token=f362164b-961d-4866-871c-a12d215d1bf7"

fun Context.createProgressDialog(message:String, isCancelable:Boolean):ProgressDialog {
    return ProgressDialog(this).apply {
        setMessage(message)
        setCancelable(isCancelable)
        setCanceledOnTouchOutside(isCancelable)
    }
}
