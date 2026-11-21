package com.moviedeck

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.moviedeck.api.CastDto
import com.moviedeck.api.RetrofitClient
import com.moviedeck.databinding.ActivityDetailBinding
import com.moviedeck.databinding.ItemCastBinding
import kotlinx.coroutines.launch

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val id = intent.getIntExtra("id", 0)
        val type = intent.getStringExtra("type") ?: "movie"
        var watchItem: WatchItem? = null

        binding.rvCast.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnShare.setOnClickListener {
            Toast.makeText(this, "Share clicked", Toast.LENGTH_SHORT).show()
        }

        binding.btnHeart.setOnClickListener {
            Toast.makeText(this, "Added to favourites", Toast.LENGTH_SHORT).show()
        }

        binding.btnSave.setOnClickListener {
            val item = watchItem
            if (item == null) {
                Toast.makeText(this, "Please wait for the data to load", Toast.LENGTH_SHORT).show()
            } else {
                val message = WatchlistStore.add(this, item)
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnTrailer.setOnClickListener {
            lifecycleScope.launch {
                try {
                    val videos = RetrofitClient.api.videos(type, id).results
                    val trailer = videos.firstOrNull { it.site == "YouTube" && it.type == "Trailer" }
                    if (trailer == null) {
                        Toast.makeText(this@DetailActivity, "No trailer found", Toast.LENGTH_SHORT).show()
                    } else {
                        val link = Uri.parse("https://www.youtube.com/watch?v=" + trailer.key)
                        startActivity(Intent(Intent.ACTION_VIEW, link))
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@DetailActivity, "Could not load trailer", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.progress.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                val detail = RetrofitClient.api.details(type, id)
                val credits = RetrofitClient.api.credits(type, id)

                val title = detail.title ?: detail.name ?: ""
                val date = detail.releaseDate ?: detail.firstAirDate ?: ""
                val year = date.take(4)
                val minutes = detail.runtime ?: detail.episodeRunTime?.firstOrNull() ?: 0

                watchItem = WatchItem(id, type, title, detail.posterPath, year)

                var runtimeText = "N/A"
                var meta = year
                if (minutes > 0) {
                    runtimeText = minutes.toString() + " min"
                    meta = year + " · " + (minutes / 60) + "h " + (minutes % 60) + "m"
                }

                var genres = ""
                if (detail.genres != null) {
                    genres = detail.genres.joinToString(" · ") { it.name }
                }

                var releaseText = "N/A"
                if (date.isNotEmpty()) {
                    releaseText = date
                }

                binding.movie = MovieInfo(
                    genres,
                    title,
                    meta,
                    String.format("%.1f", detail.voteAverage ?: 0.0),
                    (detail.voteCount ?: 0).toString() + " votes",
                    runtimeText,
                    "Runtime",
                    releaseText,
                    "Released",
                    detail.overview ?: ""
                )

                val backdrop = detail.backdropPath ?: detail.posterPath
                if (backdrop != null) {
                    Glide.with(this@DetailActivity).load("https://image.tmdb.org/t/p/w780" + backdrop).into(binding.ivBackdrop)
                }
                if (detail.posterPath != null) {
                    Glide.with(this@DetailActivity).load("https://image.tmdb.org/t/p/w500" + detail.posterPath).into(binding.ivPoster)
                }

                val cast = credits.cast.take(15)
                if (cast.isNotEmpty()) {
                    binding.tvCastTitle.visibility = View.VISIBLE
                    binding.rvCast.adapter = CastAdapter(cast)
                }

                binding.progress.visibility = View.GONE
            } catch (e: Exception) {
                binding.progress.visibility = View.GONE
                binding.tvError.text = "Could not load details: " + e.javaClass.simpleName + " - " + e.message
                binding.tvError.visibility = View.VISIBLE
            }
        }
    }
}

class CastAdapter(val items: List<CastDto>) :
    RecyclerView.Adapter<CastAdapter.MyHolder>() {

    class MyHolder(val binding: ItemCastBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val binding = ItemCastBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyHolder(binding)
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val item = items[position]
        val avatars = listOf(R.drawable.avatar_0, R.drawable.avatar_1, R.drawable.avatar_2, R.drawable.avatar_3)
        val name = item.name ?: ""

        holder.binding.avatarFrame.setBackgroundResource(avatars[position % 4])
        holder.binding.tvName.text = name
        holder.binding.tvSub.text = item.character ?: ""

        if (item.profilePath != null) {
            holder.binding.tvInitials.text = ""
            Glide.with(holder.itemView).load("https://image.tmdb.org/t/p/w185" + item.profilePath).circleCrop().into(holder.binding.ivFace)
        } else {
            holder.binding.tvInitials.text = name.take(1)
            holder.binding.ivFace.setImageDrawable(null)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}
