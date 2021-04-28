package com.example.chatapp

import android.Manifest
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.chatapp.utils.DEFAULT_AVATAR_URL
import com.example.chatapp.utils.IMAGE_FROM_GALLERY_REQUEST_CODE
import com.example.chatapp.utils.READ_WRITE_PERMISSION_REQUEST_CODE
import com.example.chatapp.utils.createProgressDialog
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {

    private val storage by lazy {
        FirebaseStorage.getInstance()
    }
    private val auth by lazy {
        FirebaseAuth.getInstance()
    }
    private val db by lazy {
        FirebaseFirestore.getInstance()
    }
    lateinit var progressDialog:ProgressDialog

    lateinit var imageUri:Uri
    var imageDownloadUrl = DEFAULT_AVATAR_URL

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
    }

    fun checkPermissionsAndChooseImage(view:View) {
        val permissionsToCheck = arrayListOf<String>()
        if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
            permissionsToCheck.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
            permissionsToCheck.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if(permissionsToCheck.size !=0)
            requestPermissions(permissionsToCheck.toTypedArray(), READ_WRITE_PERMISSION_REQUEST_CODE)
        else {
            val intent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "*/*"
            val mimes = arrayOf("image/*")
            intent.putExtra(Intent.EXTRA_MIME_TYPES,mimes)
            startActivityForResult(intent, IMAGE_FROM_GALLERY_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if( requestCode == IMAGE_FROM_GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data!=null)
        {
            data.data?.let {
                ivAvatarSignUp.setImageURI(it)
                imageUri = it
            }
        }
    }

    fun uploadAndSignUp(view: View) {
        if(etNameSignUp.text.isNullOrEmpty()){
            Toast.makeText(this,"Name cannot be empty",Toast.LENGTH_SHORT).show()
            return
        }
        btNextSignUpPage.isEnabled = false
        progressDialog = this.createProgressDialog("Signing you in...",false)
        progressDialog.show()
        uploadImage()
    }

    private fun uploadImage() {
        val firebasePathRef = storage.reference.child("uploads/${auth.uid}")
        val uploadTask = firebasePathRef.putFile(imageUri)
        uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot,Task<Uri>> { task ->
            if(!task.isSuccessful){
                task.exception?.let {
                    throw it
                }
            }
            return@Continuation firebasePathRef.downloadUrl
        }).addOnCompleteListener {
            if(it.isSuccessful) {
                imageDownloadUrl = it.result.toString()
                signUpToDatabase()
            }
        }

    }

    private fun signUpToDatabase() {
        val user = User(etNameSignUp.text.toString(),imageDownloadUrl,auth.uid!!.toString())
        db.collection("users").document(auth.uid!!.toString()).set(user)
                .addOnSuccessListener {
                    if(::progressDialog.isInitialized)
                        progressDialog.dismiss()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
                .addOnFailureListener {
                    if(::progressDialog.isInitialized)
                        progressDialog.dismiss()
                    Toast.makeText(this,"Error signing in!",Toast.LENGTH_SHORT).show()
                    btNextSignUpPage.isEnabled = true
                }
    }
}