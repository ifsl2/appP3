package com.example.appp3

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class UserActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private var fileUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        auth = FirebaseAuth.getInstance()

        setUserInfo()
        btnClicks()
    }

    private fun btnClicks(){
        val tvProfileSignOut: TextView = findViewById(R.id.tv_profile_signOut)
        val btnProfileSaveInfo: TextView = findViewById(R.id.btn_profileSaveInfo)
        //val ivProfileImage: TextView = findViewById(R.id.iv_profileImage)
        tvProfileSignOut.setOnClickListener{
            signOutUser()
        }

        btnProfileSaveInfo.setOnClickListener{
            saveUserInfo()
        }

        /*ivProfileImage.setOnClickListener{
            selectImage()
        }*/
    }

    private fun saveUserInfo(){
        val etProfileUsername: TextView = findViewById(R.id.et_profileUsername)
        val etProfileEmail: TextView = findViewById(R.id.et_profileEmail)
        auth.currentUser?.let{
            val username = etProfileUsername.text.toString()
            val userEmail = etProfileEmail.text.toString()

            val update = UserProfileChangeRequest.Builder().setDisplayName(username).build()

            GlobalScope.launch(Dispatchers.IO){
                try{
                    it.updateProfile(update).await()
                    it.updateEmail(userEmail)
                    Toast.makeText(this@UserActivity, "Atualizado", Toast.LENGTH_SHORT).show()


                }catch(e: Exception){
                    Toast.makeText(this@UserActivity, e.message, Toast.LENGTH_SHORT).show()
                }

            }

        }
    }
    private fun setUserInfo(){
        val etProfileUsername: TextView = findViewById(R.id.et_profileUsername)
        val etProfileEmail: TextView = findViewById(R.id.et_profileEmail)

        etProfileUsername.setText(auth.currentUser?.email)
        etProfileEmail.setText(auth.currentUser?.displayName)
    }

    private fun signOutUser(){
        auth.signOut()

        val i = Intent(this, MainActivity::class.java)
        startActivity(i)

        Toast.makeText(this, "Deslogado!", Toast.LENGTH_SHORT).show()

    }

    private fun selectImage(){
        ImagePicker.with(this).crop().compress(1024).maxResultSize(1080, 1000).start()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //val ivProfileImage: TextView = findViewById(R.id.iv_profileImage)
        when(resultCode){
            Activity.RESULT_OK ->{
                fileUri = data?.data
                //ivProfileImage.setImage
            }
            else ->{
                Toast.makeText(this, "Task cancelled", Toast.LENGTH_SHORT)
            }
        }
    }
}