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
import kotlinx.coroutines.withContext
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private var firstTimeUser = true
    private var fileUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

        buttonClicks()
    }
    private fun buttonClicks(){
        val btnLogin: TextView = findViewById(R.id.btn_login)
        val btnPassword: TextView = findViewById(R.id.btn_register)
        //val ivProfileImage: TextView = findViewById(R.id.iv_profileImage)
        btnLogin.setOnClickListener{
            firstTimeUser = false
            createOrLoginUser()
        }
        btnPassword.setOnClickListener{
            firstTimeUser = false
            createOrLoginUser()
        }

        /*ivProfileImage.setOnClickListener{
            selectImage()
        }*/
    }
    private fun createOrLoginUser(){
        val etEmailLogin: TextView = findViewById(R.id.et_emailLogin)
        val etPasswordLogin: TextView = findViewById(R.id.et_passwordLogin)
        val email:String = etEmailLogin.text.toString()
        val password:String = etPasswordLogin.text.toString()

        if(email.isNotEmpty() && password.isNotEmpty()){
            GlobalScope.launch(Dispatchers.IO) {
                try {
                    if(firstTimeUser){
                        auth.createUserWithEmailAndPassword(email, password).await()
                        auth.currentUser.let{
                            val update = UserProfileChangeRequest.Builder().setPhotoUri(fileUri).build()
                            it?.updateProfile(update)
                        }?.await()
                    }else{
                        auth.createUserWithEmailAndPassword(email, password).await()
                    }

                    withContext(Dispatchers.Main){
                        Toast.makeText(this@MainActivity, "Você está Logado", Toast.LENGTH_SHORT).show()

                        var i = Intent(this@MainActivity, UserActivity::class.java)
                        startActivity(i)
                        finish()
                    }
                }catch (e: Exception){
                    println(e)
                }
            }

        }
    }

    private fun checkIfUserIsLoggedIn(){
        var i = Intent(this@MainActivity, UserActivity::class.java)
        startActivity(i)
        finish()
    }

    override fun onStart() {
        super.onStart()
        checkIfUserIsLoggedIn()
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