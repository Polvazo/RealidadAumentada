package com.bbva.hackaton.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import com.bbva.hackaton.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId

class SplashScreenActivity : AppCompatActivity() {

    private val SPLASH_TIME_OUT:Long=3000 // 3 sec


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)


        Handler().postDelayed({
            // This method will be executed once the timer is over
            // Start your app main activity
            startActivity(Intent(this, LoginActivity::class.java))

            // close this activity
            finish()
        }, SPLASH_TIME_OUT)
    }

}