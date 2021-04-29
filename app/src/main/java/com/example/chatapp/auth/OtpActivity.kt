package com.example.chatapp.auth

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.chatapp.util.KEY_PHONE_NUMBER
import com.example.chatapp.R
import com.example.chatapp.util.createProgressDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_otp.*
import java.util.concurrent.TimeUnit

class OtpActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var auth:FirebaseAuth
    private lateinit var firebaseAuthCallbacks:PhoneAuthProvider.OnVerificationStateChangedCallbacks
    var mVerificationId:String? = null
    var mResendToken:PhoneAuthProvider.ForceResendingToken? = null
    var mCountDownTimer:CountDownTimer? = null
    lateinit var progressDialog:ProgressDialog

    private lateinit var phoneNumber:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)
        auth = Firebase.auth
        initViews()
    }

    private fun initViews() {
        phoneNumber = intent.getStringExtra(KEY_PHONE_NUMBER)?:""
        tvVerifyNumberOtpActivity.text = getString(R.string.verify_number_text_view,phoneNumber)
        btVerifyCode.setOnClickListener(this)
        btResendSMS.setOnClickListener(this)
        setSpanningStringForWrongNumber()
        initAuthentication()
    }

    /* Authentication part begins */

    private fun initAuthentication() {
        firebaseAuthCallbacks = object: PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                if(!credential.smsCode.isNullOrEmpty())
                    etOtp.setText(credential.smsCode)
                if(::progressDialog.isInitialized)
                    progressDialog.dismiss()
                signInWithPhoneAuthCredentials(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                if (e is FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(this@OtpActivity, "Wrong code", Toast.LENGTH_SHORT).show()
                    etOtp.text.clear()
                }else if (e is FirebaseTooManyRequestsException){
                    Toast.makeText(this@OtpActivity,"Too many incorrect attempts!!\n Please try after some time",Toast.LENGTH_SHORT).show()
                    finishAffinity()
                }
                if(::progressDialog.isInitialized)
                    progressDialog.dismiss()
                notifyUserForRetry(message="Your phone number might be wrong or Connection Error. Try again!")
            }

            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                mVerificationId = verificationId
                mResendToken = token
                if(::progressDialog.isInitialized)
                    progressDialog.dismiss()
            }
        }
        verifyPhoneNumber()
    }

    private fun signInWithPhoneAuthCredentials(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener {
                    if(::progressDialog.isInitialized)
                        progressDialog.dismiss()
                    if (it.isSuccessful) {
                        startActivity(Intent(this, SignUpActivity::class.java))
                        finish()
                    }else {
                        notifyUserForRetry("Invalid Verification code")
                    }
                }
    }

    private fun verifyPhoneNumber() {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60,TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(firebaseAuthCallbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
        showAndStartTimer(60000,1000)
        progressDialog = this.createProgressDialog("Sending verification code",false)
        progressDialog.show()
    }

    /* Authentication part finishes */
    private fun notifyUserForRetry(message: String) {
        MaterialAlertDialogBuilder(this).apply {
            setMessage(message)
            setPositiveButton("OK"){ _ , _->
                startLoginActivityAgain()
            }
            setNegativeButton("Cancel"){ dialog , _ ->
                dialog.dismiss()
            }
            setCancelable(false)
            create()
            show()
        }
    }

    private fun showAndStartTimer(millSecondsInFuture:Long,countDownInterval:Long) {
        btResendSMS.isEnabled = false
        mCountDownTimer = object : CountDownTimer(millSecondsInFuture,countDownInterval){
            override fun onTick(millisUntilFinished: Long) {
                tvOtpResendCounter.text = getString(R.string.otp_counter,millisUntilFinished/countDownInterval)
                tvOtpResendCounter.visibility = View.VISIBLE
            }

            override fun onFinish() {
                btResendSMS.isEnabled = true
                tvOtpResendCounter.visibility = View.GONE
            }
        }.start()
    }

    private fun startLoginActivityAgain() {
        startActivity(Intent(this@OtpActivity, LoginActivity::class.java)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
    }

    private fun setSpanningStringForWrongNumber() {
        val span = SpannableString(getString(R.string.waiting_for_otp_text_view,phoneNumber))
        val clickableSpan = object : ClickableSpan(){
            override fun onClick(widget: View) {
                startLoginActivityAgain()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
                ds.color = ContextCompat.getColor(this@OtpActivity, R.color.light_green)
            }
        }
        span.setSpan(clickableSpan,span.length-13,span.length,SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
        tvWaitingForOtp.movementMethod = LinkMovementMethod.getInstance()
        tvWaitingForOtp.text = span
    }

    override fun onBackPressed() {
        /* DO NOTHING */
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mCountDownTimer != null)
            mCountDownTimer!!.cancel()
    }

    override fun onClick(v: View?) {
        when(v)
        {
            btVerifyCode->{
                val smsCode = etOtp.text
                if(smsCode.isNotEmpty() && !mVerificationId.isNullOrEmpty()) {
                    progressDialog = this.createProgressDialog("Please wait...", false)
                    progressDialog.show()
                    val credentials = PhoneAuthProvider.getCredential(mVerificationId!!, smsCode.toString())
                    signInWithPhoneAuthCredentials(credentials)
                }
            }
            btResendSMS->{
                if(mResendToken!=null) {
                    showAndStartTimer(60000, 10000)
                    progressDialog = this.createProgressDialog("Sending verification code again...", false)
                    progressDialog.show()
                    val options = PhoneAuthOptions.newBuilder(auth)
                            .setPhoneNumber(phoneNumber)
                            .setTimeout(60,TimeUnit.SECONDS)
                            .setActivity(this)
                            .setCallbacks(firebaseAuthCallbacks)
                            .setForceResendingToken(mResendToken!!)
                            .build()
                    PhoneAuthProvider.verifyPhoneNumber(options)
                }
            }
        }
    }
}