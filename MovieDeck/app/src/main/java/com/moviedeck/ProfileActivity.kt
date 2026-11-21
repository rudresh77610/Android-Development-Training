package com.moviedeck

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.moviedeck.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {

    lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.cardWatchlist.setOnClickListener {
            val intent = Intent(this, WatchlistActivity::class.java)
            startActivity(intent)
        }

        binding.btnSignOut.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            finishAffinity()
        }
    }

    override fun onResume() {
        super.onResume()

        val prefs = getSharedPreferences("user", MODE_PRIVATE)
        val name = prefs.getString("name", "") ?: ""
        val email = prefs.getString("email", "") ?: ""
        val recents = prefs.getString("recents", "") ?: ""

        var searchCount = 0
        if (recents.isNotEmpty()) {
            searchCount = recents.split("|").size
        }
        val watchCount = WatchlistStore.getAll(this).size

        binding.profile = ProfileInfo(
            name.take(1).uppercase(),
            name,
            email,
            watchCount.toString(),
            searchCount.toString()
        )
    }
}
