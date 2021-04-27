package com.example.chatapp.utils

import android.app.ProgressDialog
import android.content.Context

const val PHONE_NUMBER_REQUEST_CODE = 11923

const val KEY_PHONE_NUMBER = "phoneNumber"

fun Context.createProgressDialog(message:String, isCancelable:Boolean):ProgressDialog {
    return ProgressDialog(this).apply {
        setMessage(message)
        setCancelable(isCancelable)
        setCanceledOnTouchOutside(isCancelable)
    }
}
