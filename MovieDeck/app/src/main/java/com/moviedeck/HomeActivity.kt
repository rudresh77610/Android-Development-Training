package com.moviedeck

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.moviedeck.api.MediaItem
import com.moviedeck.api.RetrofitClient
import com.moviedeck.databinding.ActivityHomeBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeActivity : AppCompatActivity() {

    var currentTab = 0
    var currentChip = 0
    var heroItem: MediaItem? = null

    val movieGenres = listOf(0, 28, 18, 878, 35)
    val tvGenres = listOf(0, 10759, 18, 10765, 35)
    val genreNames = listOf("All", "Action", "Drama", "Sci-Fi", "Comedy")

    lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // setContentView(R.layout.activity_home)
        // val date = findViewById<TextView>(R.id.tvDate)
        // val greeting = findViewById<TextView>(R.id.tvGreeting)
        // val avatar = findViewById<TextView>(R.id.tvAvatar)
        // val search = findViewById<View>(R.id.btnSearch)
        // val watchlist = findViewById<View>(R.id.btnWatchlist)
        // val heroTrailer = findViewById<View>(R.id.heroTrailer)
        // val heroAdd = findViewById<View>(R.id.heroAdd)
        // val seeAll = findViewById<View>(R.id.seeAll)
        // tabMovies = findViewById(R.id.tabMovies)
        // tabTv = findViewById(R.id.tabTv)
        // tabPeople = findViewById(R.id.tabPeople)
        // chips = findViewById(R.id.chips)
        // progress = findViewById(R.id.progress)
        // error = findViewById(R.id.tvError)
        // content = findViewById(R.id.mediaContent)
        // hero = findViewById(R.id.hero)
        // heroImage = findViewById(R.id.heroImage)
        // heroBadge = findViewById(R.id.heroBadge)
        // heroTitle = findViewById(R.id.heroTitle)
        // heroMeta = findViewById(R.id.heroMeta)
        // sectionTitle = findViewById(R.id.sectionTitle)
        // trending = findViewById(R.id.rvTrending)

        val today = SimpleDateFormat("EEE, MMM d", Locale.getDefault()).format(Date())

        val prefs = getSharedPreferences("user", MODE_PRIVATE)
        val fullName = prefs.getString("name", "") ?: ""
        val firstName = fullName.trim().substringBefore(" ")

        binding.homeInfo = HomeInfo(today, "Hey, " + firstName, fullName.take(1).uppercase())
        binding.heroInfo = HeroInfo("", "", "", "Trending now")

        binding.rvTrending.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        binding.tabMovies.setOnClickListener {
            currentTab = 0
            currentChip = 0
            showTabs()
            showChips()
            loadData()
        }

        binding.tabTv.setOnClickListener {
            currentTab = 1
            currentChip = 0
            showTabs()
            showChips()
            loadData()
        }

        binding.tabPeople.setOnClickListener {
            currentTab = 2
            currentChip = 0
            showTabs()
            showChips()
            loadData()
        }

        binding.btnSearch.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
        }

        binding.tvAvatar.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        binding.btnWatchlist.setOnClickListener {
            val intent = Intent(this, WatchlistActivity::class.java)
            startActivity(intent)
        }

        binding.heroAdd.setOnClickListener {
            val item = heroItem
            val type = getType()
            if (item == null) {
                Toast.makeText(this, "Please wait for the data to load", Toast.LENGTH_SHORT).show()
            } else if (type == "person") {
                Toast.makeText(this, "Only movies and TV shows can be added", Toast.LENGTH_SHORT).show()
            } else {
                val watchItem = WatchItem(item.id, type, item.displayTitle, item.posterPath, item.year)
                val message = WatchlistStore.add(this, watchItem)
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }

        binding.hero.setOnClickListener {
            openHero()
        }

        binding.heroTrailer.setOnClickListener {
            openHero()
        }

        binding.seeAll.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
        }

        showTabs()
        showChips()
        loadData()
    }

    fun getType(): String {
        if (currentTab == 1) {
            return "tv"
        }
        if (currentTab == 2) {
            return "person"
        }
        return "movie"
    }

    fun openHero() {
        val item = heroItem
        val type = getType()
        if (item == null) {
            return
        }
        if (type == "person") {
            Toast.makeText(this, "Person details are not available yet", Toast.LENGTH_SHORT).show()
        } else {
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("id", item.id)
            intent.putExtra("type", type)
            startActivity(intent)
        }
    }

    fun showTabs() {
        val tabs = listOf(binding.tabMovies, binding.tabTv, binding.tabPeople)
        for (i in tabs.indices) {
            if (i == currentTab) {
                tabs[i].setBackgroundResource(R.drawable.bg_segment_selected)
                tabs[i].setTextColor(getColor(R.color.white))
            } else {
                tabs[i].background = null
                tabs[i].setTextColor(getColor(R.color.text_secondary))
            }
        }
    }

    fun showChips() {
        binding.chips.removeAllViews()
        var names = genreNames
        if (currentTab == 2) {
            names = listOf("Trending")
        }
        for (i in names.indices) {
            val chip = LayoutInflater.from(this).inflate(R.layout.item_chip, binding.chips, false) as TextView
            chip.text = names[i]
            chip.isSelected = (i == currentChip)
            chip.setOnClickListener {
                currentChip = i
                showChips()
                loadData()
            }
            binding.chips.addView(chip)
        }
    }

    fun loadData() {
        binding.progress.visibility = View.VISIBLE
        binding.tvError.visibility = View.GONE

        lifecycleScope.launch {
            try {
                val type = getType()
                var genre = 0
                if (currentTab == 0) {
                    genre = movieGenres[currentChip]
                }
                if (currentTab == 1) {
                    genre = tvGenres[currentChip]
                }

                val response = if (genre == 0) {
                    RetrofitClient.api.trending(type)
                } else {
                    RetrofitClient.api.discover(type, genre.toString())
                }

                showList(response.results, type)
                binding.progress.visibility = View.GONE
            } catch (e: Exception) {
                binding.progress.visibility = View.GONE
                binding.mediaContent.visibility = View.GONE
                binding.tvError.text = "Could not load data: " + e.javaClass.simpleName + " - " + e.message
                binding.tvError.visibility = View.VISIBLE
            }
        }
    }

    fun showList(list: List<MediaItem>, type: String) {
        binding.mediaContent.visibility = View.VISIBLE

        var first = list.firstOrNull { it.backdropPath != null }
        if (first == null) {
            first = list.firstOrNull()
        }
        heroItem = first

        var sectionText = "Trending now"
        if (currentChip != 0) {
            sectionText = genreNames[currentChip]
        }

        if (first == null) {
            binding.hero.visibility = View.GONE
            binding.heroInfo = HeroInfo("", "", "", sectionText)
        } else {
            binding.hero.visibility = View.VISIBLE
            val path = first.backdropPath ?: first.posterPath ?: first.profilePath
            if (path != null) {
                Glide.with(this).load("https://image.tmdb.org/t/p/w780" + path).into(binding.heroImage)
            } else {
                binding.heroImage.setImageDrawable(null)
            }

            var badge = ""
            var meta = ""
            if (type == "person") {
                badge = "Trending"
                meta = first.knownForDepartment ?: ""
            } else {
                if (type == "movie") {
                    badge = "In cinemas"
                } else {
                    badge = "On air"
                }
                val rating = String.format("%.1f", first.voteAverage ?: 0.0)
                meta = first.year + " · ★ " + rating
            }
            binding.heroInfo = HeroInfo(badge, first.displayTitle, meta, sectionText)
        }

        val rest = list.filter { it.id != first?.id }
        binding.rvTrending.adapter = PosterAdapter(rest, type)
    }
}

class PosterAdapter(val items: List<MediaItem>, val type: String) :
    RecyclerView.Adapter<PosterAdapter.MyHolder>() {

    class MyHolder(view: View) : RecyclerView.ViewHolder(view) {
        val poster = view.findViewById<ImageView>(R.id.ivPoster)
        val title = view.findViewById<TextView>(R.id.tvTitle)
        val sub = view.findViewById<TextView>(R.id.tvSub)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_poster_h, parent, false)
        return MyHolder(view)
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val item = items[position]
        val kind = item.mediaType ?: type

        holder.title.text = item.displayTitle
        if (kind == "person") {
            holder.sub.text = item.knownForDepartment ?: ""
        } else {
            holder.sub.text = item.year
        }

        val path = item.posterPath ?: item.profilePath
        if (path != null) {
            Glide.with(holder.itemView).load("https://image.tmdb.org/t/p/w500" + path).into(holder.poster)
            holder.title.visibility = View.GONE
            holder.sub.visibility = View.GONE
        } else {
            holder.poster.setImageDrawable(null)
            holder.title.visibility = View.VISIBLE
            holder.sub.visibility = View.VISIBLE
        }

        holder.itemView.setOnLongClickListener {
            val context = holder.itemView.context
            if (kind == "person") {
                Toast.makeText(context, "Only movies and TV shows can be added", Toast.LENGTH_SHORT).show()
            } else {
                val watchItem = WatchItem(item.id, kind, item.displayTitle, item.posterPath, item.year)
                val message = WatchlistStore.add(context, watchItem)
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
            true
        }

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            if (kind == "person") {
                Toast.makeText(context, "Person details are not available yet", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(context, DetailActivity::class.java)
                intent.putExtra("id", item.id)
                intent.putExtra("type", kind)
                context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}
