package com.moviedeck

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SignUpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        val back = findViewById<ImageView>(R.id.back)
        val name = findViewById<EditText>(R.id.name)
        val email = findViewById<EditText>(R.id.email)
        val password = findViewById<EditText>(R.id.password)
        val terms = findViewById<CheckBox>(R.id.terms)
        val error = findViewById<TextView>(R.id.error)
        val submit = findViewById<TextView>(R.id.submit)
        val signInLink = findViewById<TextView>(R.id.sign_in_link)

        back.setOnClickListener {
            finish()
        }

        submit.setOnClickListener {
            val nameText = name.text.toString()
            val emailText = email.text.toString()
            val passwordText = password.text.toString()

            if (nameText.isEmpty() || emailText.isEmpty() || passwordText.isEmpty()) {
                error.text = "Please fill all fields"
                error.visibility = View.VISIBLE
            } else if (passwordText.length < 6) {
                error.text = "Password must be at least 6 characters"
                error.visibility = View.VISIBLE
            } else if (!terms.isChecked) {
                error.text = "Please accept the terms"
                error.visibility = View.VISIBLE
            } else {
                error.visibility = View.GONE
                Toast.makeText(this, "Account created", Toast.LENGTH_SHORT).show()
            }
        }

        signInLink.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
