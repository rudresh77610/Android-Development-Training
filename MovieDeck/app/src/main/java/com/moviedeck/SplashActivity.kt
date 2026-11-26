package com.moviedeck

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val prefs = getSharedPreferences("user", MODE_PRIVATE)
        val loggedIn = prefs.getBoolean("loggedIn", false)
        val savedEmail = prefs.getString("email", "") ?: ""

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = if (loggedIn) {
                Intent(this, HomeActivity::class.java)
            } else if (savedEmail.isNotEmpty()) {
                Intent(this, SignInActivity::class.java)
            } else {
                Intent(this, OnboardingOneActivity::class.java)
            }
            startActivity(intent)
            finish()
        }, 2000)
    }
}
