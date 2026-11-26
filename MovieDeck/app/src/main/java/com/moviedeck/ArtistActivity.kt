package com.moviedeck

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.moviedeck.api.RetrofitClient
import com.moviedeck.databinding.ActivityArtistBinding
import kotlinx.coroutines.launch

class ArtistActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityArtistBinding.inflate(layoutInflater)
        setContentView(binding.root)
        BottomNav.attach(this)

        val id = intent.getIntExtra("id", 0)

        binding.rvFilmography.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnShare.setOnClickListener {
            Toast.makeText(this, "Share clicked", Toast.LENGTH_SHORT).show()
        }

        binding.btnFollow.setOnClickListener {
            Toast.makeText(this, "Followed", Toast.LENGTH_SHORT).show()
        }

        binding.btnMail.setOnClickListener {
            Toast.makeText(this, "Message clicked", Toast.LENGTH_SHORT).show()
        }

        binding.progress.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                val person = RetrofitClient.api.person(id)
                val credits = RetrofitClient.api.personCredits(id)

                val name = person.name ?: ""
                val parts = name.split(" ")
                var initials = ""
                for (part in parts.take(2)) {
                    initials = initials + part.take(1)
                }

                val films = credits.cast.sortedByDescending { it.popularity ?: 0.0 }.take(10)

                binding.artist = ArtistInfo(
                    initials,
                    person.knownForDepartment ?: "",
                    name,
                    "★ " + String.format("%.1f", person.popularity ?: 0.0),
                    credits.cast.size.toString() + " credits",
                    person.birthday ?: "N/A",
                    person.placeOfBirth ?: "N/A",
                    person.knownForDepartment ?: "N/A",
                    credits.cast.size.toString(),
                    person.biography ?: "",
                    films.size.toString() + " titles"
                )

                if (person.profilePath != null) {
                    Glide.with(this@ArtistActivity).load("https://image.tmdb.org/t/p/w500" + person.profilePath).into(binding.ivPhoto)
                }

                binding.rvFilmography.adapter = PosterAdapter(films, "movie")
                binding.progress.visibility = View.GONE
            } catch (e: Exception) {
                binding.progress.visibility = View.GONE
                binding.tvError.text = "Could not load details: " + e.javaClass.simpleName + " - " + e.message
                binding.tvError.visibility = View.VISIBLE
            }
        }
    }
}
