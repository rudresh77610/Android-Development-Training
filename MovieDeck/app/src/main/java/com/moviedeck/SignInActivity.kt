package com.moviedeck

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SignInActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        BottomNav.attach(this)

        val back = findViewById<ImageView>(R.id.back)
        val email = findViewById<EditText>(R.id.email)
        val password = findViewById<EditText>(R.id.password)
        val forgot = findViewById<TextView>(R.id.forgot)
        val error = findViewById<TextView>(R.id.error)
        val submit = findViewById<TextView>(R.id.submit)
        val google = findViewById<TextView>(R.id.google)
        val apple = findViewById<TextView>(R.id.apple)
        val facebook = findViewById<TextView>(R.id.facebook)
        val createAccount = findViewById<TextView>(R.id.create_account)

        back.setOnClickListener {
            finish()
        }

        forgot.setOnClickListener {
            Toast.makeText(this, "Forgot password clicked", Toast.LENGTH_SHORT).show()
        }

        google.setOnClickListener {
            Toast.makeText(this, "Google clicked", Toast.LENGTH_SHORT).show()
        }

        apple.setOnClickListener {
            Toast.makeText(this, "Apple clicked", Toast.LENGTH_SHORT).show()
        }

        facebook.setOnClickListener {
            Toast.makeText(this, "Facebook clicked", Toast.LENGTH_SHORT).show()
        }

        submit.setOnClickListener {
            val emailText = email.text.toString()
            val passwordText = password.text.toString()

            if (emailText.isEmpty() || passwordText.isEmpty()) {
                error.text = "Please fill all fields"
                error.visibility = View.VISIBLE
            } else {
                val prefs = getSharedPreferences("user", MODE_PRIVATE)
                val savedEmail = prefs.getString("email", "")
                val savedPassword = prefs.getString("password", "")

                if (savedEmail == emailText && savedPassword != passwordText) {
                    error.text = "Wrong password"
                    error.visibility = View.VISIBLE
                } else {
                    if (savedEmail != emailText) {
                        val editor = prefs.edit()
                        editor.putString("name", emailText.substringBefore("@"))
                        editor.putString("email", emailText)
                        editor.putString("password", passwordText)
                        editor.apply()
                    }

                    prefs.edit().putBoolean("loggedIn", true).apply()

                    error.visibility = View.GONE
                    Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }

        createAccount.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
