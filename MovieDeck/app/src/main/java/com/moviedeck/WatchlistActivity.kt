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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.moviedeck.databinding.ActivityWatchlistBinding
import com.moviedeck.databinding.ItemChipBinding

class WatchlistActivity : AppCompatActivity() {

    var currentFilter = 0
    lateinit var binding: ActivityWatchlistBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWatchlistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvGrid.layoutManager = GridLayoutManager(this, 2)

        binding.btnAdd.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        showList()
    }

    fun showList() {
        val all = WatchlistStore.getAll(this)
        val movies = all.filter { it.type == "movie" }
        val shows = all.filter { it.type == "tv" }

        binding.tvCount.text = all.size.toString() + " of " + WatchlistStore.MAX + " titles"

        binding.chips.removeAllViews()
        val names = listOf("All · " + all.size, "Movies · " + movies.size, "TV · " + shows.size)
        for (i in names.indices) {
            val chip = ItemChipBinding.inflate(layoutInflater, binding.chips, false).root
            chip.text = names[i]
            chip.isSelected = (i == currentFilter)
            chip.setOnClickListener {
                currentFilter = i
                showList()
            }
            binding.chips.addView(chip)
        }

        var list = all
        if (currentFilter == 1) {
            list = movies.toMutableList()
        }
        if (currentFilter == 2) {
            list = shows.toMutableList()
        }

        binding.rvGrid.adapter = WatchlistAdapter(list, this)

        if (list.isEmpty()) {
            binding.tvEmpty.visibility = View.VISIBLE
        } else {
            binding.tvEmpty.visibility = View.GONE
        }
    }
}

class WatchlistAdapter(val items: List<WatchItem>, val activity: WatchlistActivity) :
    RecyclerView.Adapter<WatchlistAdapter.MyHolder>() {

    class MyHolder(view: View) : RecyclerView.ViewHolder(view) {
        val poster = view.findViewById<ImageView>(R.id.ivPoster)
        val title = view.findViewById<TextView>(R.id.tvTitle)
        val sub = view.findViewById<TextView>(R.id.tvSub)
        val heart = view.findViewById<View>(R.id.btnHeart)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_watch_grid, parent, false)
        return MyHolder(view)
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val item = items[position]

        holder.title.text = item.title
        if (item.type == "movie") {
            holder.sub.text = "Movie · " + item.year
        } else {
            holder.sub.text = "TV · " + item.year
        }

        if (item.posterPath != null) {
            Glide.with(holder.itemView).load("https://image.tmdb.org/t/p/w342" + item.posterPath).into(holder.poster)
        } else {
            holder.poster.setImageDrawable(null)
        }

        holder.heart.setOnClickListener {
            WatchlistStore.remove(activity, item)
            Toast.makeText(activity, "Removed from watchlist", Toast.LENGTH_SHORT).show()
            activity.showList()
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(activity, DetailActivity::class.java)
            intent.putExtra("id", item.id)
            intent.putExtra("type", item.type)
            activity.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}
