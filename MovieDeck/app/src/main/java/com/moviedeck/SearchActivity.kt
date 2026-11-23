package com.moviedeck

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.moviedeck.api.MediaItem
import com.moviedeck.api.RetrofitClient
import kotlinx.coroutines.launch

class SearchActivity : AppCompatActivity() {

    var currentFilter = 0
    var allResults: List<MediaItem> = emptyList()
    var query = ""

    lateinit var searchBox: EditText
    lateinit var chips: LinearLayout
    lateinit var resultsLabel: TextView
    lateinit var message: TextView
    lateinit var progress: ProgressBar
    lateinit var results: RecyclerView
    lateinit var recents: LinearLayout
    lateinit var recentLabel: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        searchBox = findViewById(R.id.etSearch)
        chips = findViewById(R.id.chips)
        resultsLabel = findViewById(R.id.tvResultsLabel)
        message = findViewById(R.id.tvMessage)
        progress = findViewById(R.id.progress)
        results = findViewById(R.id.rvResults)
        val clear = findViewById<View>(R.id.btnClear)
        recents = findViewById(R.id.recents)
        recentLabel = findViewById(R.id.tvRecentLabel)

        results.layoutManager = LinearLayoutManager(this)

        showChips()

        showRecents()

        searchBox.setOnEditorActionListener { _, _, _ ->
            search(searchBox.text.toString().trim())
            true
        }

        searchBox.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty()) {
                    clear.visibility = View.GONE
                } else {
                    clear.visibility = View.VISIBLE
                }
            }

            override fun afterTextChanged(s: android.text.Editable?) {
            }
        })

        clear.setOnClickListener {
            searchBox.setText("")
            allResults = emptyList()
            query = ""
            showResults()
        }
    }

    fun getRecents(): MutableList<String> {
        val prefs = getSharedPreferences("user", MODE_PRIVATE)
        val saved = prefs.getString("recents", "") ?: ""
        if (saved.isEmpty()) {
            return mutableListOf()
        }
        return saved.split("|").toMutableList()
    }

    fun saveRecents(list: List<String>) {
        val prefs = getSharedPreferences("user", MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putString("recents", list.joinToString("|"))
        editor.apply()
    }

    fun addRecent(text: String) {
        val clean = text.replace("|", " ")
        val list = getRecents()
        list.remove(clean)
        list.add(0, clean)
        if (list.size > 5) {
            list.removeAt(5)
        }
        saveRecents(list)
        showRecents()
    }

    fun showRecents() {
        recents.removeAllViews()
        val list = getRecents()

        if (list.isEmpty()) {
            recentLabel.visibility = View.GONE
        } else {
            recentLabel.visibility = View.VISIBLE
        }

        for (text in list) {
            val row = LayoutInflater.from(this).inflate(R.layout.item_recent, recents, false)
            val recentText = row.findViewById<TextView>(R.id.tvQuery)
            val remove = row.findViewById<View>(R.id.ivRemove)
            recentText.text = text
            row.setOnClickListener {
                searchBox.setText(text)
                search(text)
            }
            remove.setOnClickListener {
                val newList = getRecents()
                newList.remove(text)
                saveRecents(newList)
                showRecents()
            }
            recents.addView(row)
        }
    }

    fun showChips() {
        chips.removeAllViews()
        val names = listOf("All", "Movies", "TV", "People")
        for (i in names.indices) {
            val chip = LayoutInflater.from(this).inflate(R.layout.item_chip, chips, false) as TextView
            chip.text = names[i]
            chip.isSelected = (i == currentFilter)
            chip.setOnClickListener {
                currentFilter = i
                showChips()
                showResults()
            }
            chips.addView(chip)
        }
    }

    fun search(text: String) {
        if (text.length < 2) {
            Toast.makeText(this, "Type at least 2 letters", Toast.LENGTH_SHORT).show()
            return
        }
        query = text
        addRecent(text)
        progress.visibility = View.VISIBLE
        message.visibility = View.GONE

        lifecycleScope.launch {
            try {
                allResults = RetrofitClient.api.search(query).results
                progress.visibility = View.GONE
                showResults()
            } catch (e: Exception) {
                progress.visibility = View.GONE
                results.adapter = SearchAdapter(emptyList())
                message.text = "Could not search: " + e.javaClass.simpleName + " - " + e.message
                message.visibility = View.VISIBLE
            }
        }
    }

    fun showResults() {
        val list = allResults.filter {
            when (currentFilter) {
                1 -> it.mediaType == "movie"
                2 -> it.mediaType == "tv"
                3 -> it.mediaType == "person"
                else -> it.mediaType == "movie" || it.mediaType == "tv" || it.mediaType == "person"
            }
        }
        results.adapter = SearchAdapter(list)

        if (query.isEmpty()) {
            resultsLabel.visibility = View.GONE
            message.visibility = View.GONE
        } else {
            resultsLabel.text = list.size.toString() + " results for \"" + query + "\""
            resultsLabel.visibility = View.VISIBLE
            if (list.isEmpty()) {
                message.text = "Nothing found. Try another title or name."
                message.visibility = View.VISIBLE
            } else {
                message.visibility = View.GONE
            }
        }
    }
}

class SearchAdapter(val items: List<MediaItem>) :
    RecyclerView.Adapter<SearchAdapter.MyHolder>() {

    class MyHolder(view: View) : RecyclerView.ViewHolder(view) {
        val poster = view.findViewById<ImageView>(R.id.ivPoster)
        val title = view.findViewById<TextView>(R.id.tvTitle)
        val meta = view.findViewById<TextView>(R.id.tvMeta)
        val rating = view.findViewById<TextView>(R.id.tvRating)
        val add = view.findViewById<View>(R.id.btnAdd)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_search_result, parent, false)
        return MyHolder(view)
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val item = items[position]
        val kind = item.mediaType ?: "movie"

        holder.title.text = item.displayTitle

        if (kind == "person") {
            holder.meta.text = "Person · " + (item.knownForDepartment ?: "")
            holder.rating.visibility = View.GONE
            holder.add.visibility = View.GONE
        } else {
            if (kind == "movie") {
                holder.meta.text = "Movie · " + item.year
            } else {
                holder.meta.text = "TV · " + item.year
            }
            holder.rating.text = String.format("%.1f", item.voteAverage ?: 0.0)
            holder.rating.visibility = View.VISIBLE
            holder.add.visibility = View.VISIBLE
        }

        val path = item.posterPath ?: item.profilePath
        if (path != null) {
            Glide.with(holder.itemView).load("https://image.tmdb.org/t/p/w185" + path).into(holder.poster)
        } else {
            holder.poster.setImageDrawable(null)
        }

        holder.add.setOnClickListener {
            val watchItem = WatchItem(item.id, kind, item.displayTitle, item.posterPath, item.year)
            val message = WatchlistStore.add(holder.itemView.context, watchItem)
            Toast.makeText(holder.itemView.context, message, Toast.LENGTH_SHORT).show()
        }

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            if (kind == "person") {
                val intent = Intent(context, ArtistActivity::class.java)
                intent.putExtra("id", item.id)
                context.startActivity(intent)
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
