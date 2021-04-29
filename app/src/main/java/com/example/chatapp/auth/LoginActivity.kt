package com.example.chatapp.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import com.example.chatapp.util.KEY_PHONE_NUMBER
import com.example.chatapp.util.PHONE_NUMBER_REQUEST_CODE
import com.example.chatapp.R
import com.google.android.gms.auth.api.credentials.Credential
import com.google.android.gms.auth.api.credentials.Credentials
import com.google.android.gms.auth.api.credentials.CredentialsOptions
import com.google.android.gms.auth.api.credentials.HintRequest
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    lateinit var phoneNumber:String
    lateinit var countryCode:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        requestPhoneNumberFromHints()
        etPhoneNumberLoginActivity.addTextChangedListener {
            btSubmitPhoneNumber.isEnabled = !(it.isNullOrEmpty() || it.length!=10)
        }
    }

    private fun requestPhoneNumberFromHints() {
        val hintRequest = HintRequest.Builder()
            .setPhoneNumberIdentifierSupported(true)
            .build()

        val credentialOptions = CredentialsOptions.Builder()
            .forceEnableSaveDialog()
            .build()

        val credentialsClient = Credentials.getClient(applicationContext,credentialOptions)
        val hintIntent = credentialsClient.getHintPickerIntent(hintRequest)
        startIntentSenderForResult(hintIntent.intentSender, PHONE_NUMBER_REQUEST_CODE,null,0,0,0,
            Bundle()
        )
    }

    fun showConfirmationDialog(view:View) {
        countryCode = ccpCountryCOde.selectedCountryCodeWithPlus
        phoneNumber = countryCode + etPhoneNumberLoginActivity.text.toString()
        MaterialAlertDialogBuilder(this).apply {
            setMessage("We will be verifying your phone number: $phoneNumber \n" +
                    "Is this OK, or would you like" +
                    "to edit the number?")
            setPositiveButton("OK") { _, _->
                startActivity(Intent(this@LoginActivity, OtpActivity::class.java).putExtra(
                    KEY_PHONE_NUMBER,phoneNumber))
            }
            setNegativeButton("EDIT"){ dialog, _ ->
                dialog.dismiss()
            }
            setCancelable(false)
            create()
            show()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == PHONE_NUMBER_REQUEST_CODE && resultCode == RESULT_OK && data!=null)
        {
            val credential =  data.getParcelableExtra<Credential>(Credential.EXTRA_KEY)
            credential?.apply {
                if(this.id.length==13)
                    etPhoneNumberLoginActivity.setText(credential.id.substring(3))
                else
                    etPhoneNumberLoginActivity.setText(credential.id)

            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}