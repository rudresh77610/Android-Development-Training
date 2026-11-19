package com.moviedeck

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val date = findViewById<TextView>(R.id.tvDate)
        val greeting = findViewById<TextView>(R.id.tvGreeting)
        val avatar = findViewById<TextView>(R.id.tvAvatar)
        val search = findViewById<View>(R.id.btnSearch)
        val tabMovies = findViewById<TextView>(R.id.tabMovies)
        val tabTv = findViewById<TextView>(R.id.tabTv)
        val tabPeople = findViewById<TextView>(R.id.tabPeople)
        val chips = findViewById<LinearLayout>(R.id.chips)
        val heroBadge = findViewById<TextView>(R.id.heroBadge)
        val heroTitle = findViewById<TextView>(R.id.heroTitle)
        val heroMeta = findViewById<TextView>(R.id.heroMeta)
        val heroTrailer = findViewById<View>(R.id.heroTrailer)
        val heroAdd = findViewById<View>(R.id.heroAdd)
        val seeAll = findViewById<View>(R.id.seeAll)
        val trending = findViewById<RecyclerView>(R.id.rvTrending)

        date.text = "Mon, Jan 1"
        greeting.text = "Hey, Rudresh"
        avatar.text = "R"
        heroBadge.text = "In cinemas"
        heroTitle.text = "The Last Horizon"
        heroMeta.text = "2024 · Sci-Fi · ★ 8.1"

        val names = listOf("All", "Action", "Drama", "Sci-Fi", "Comedy")
        for (i in names.indices) {
            val chip = LayoutInflater.from(this).inflate(R.layout.item_chip, chips, false) as TextView
            chip.text = names[i]
            if (i == 0) {
                chip.isSelected = true
            }
            chip.setOnClickListener {
                for (j in 0 until chips.childCount) {
                    chips.getChildAt(j).isSelected = false
                }
                chip.isSelected = true
            }
            chips.addView(chip)
        }

        tabMovies.setBackgroundResource(R.drawable.bg_segment_selected)
        tabMovies.setTextColor(getColor(R.color.white))
        tabTv.setTextColor(getColor(R.color.text_secondary))
        tabPeople.setTextColor(getColor(R.color.text_secondary))

        tabMovies.setOnClickListener {
            tabMovies.setBackgroundResource(R.drawable.bg_segment_selected)
            tabMovies.setTextColor(getColor(R.color.white))
            tabTv.background = null
            tabTv.setTextColor(getColor(R.color.text_secondary))
            tabPeople.background = null
            tabPeople.setTextColor(getColor(R.color.text_secondary))
        }

        tabTv.setOnClickListener {
            tabTv.setBackgroundResource(R.drawable.bg_segment_selected)
            tabTv.setTextColor(getColor(R.color.white))
            tabMovies.background = null
            tabMovies.setTextColor(getColor(R.color.text_secondary))
            tabPeople.background = null
            tabPeople.setTextColor(getColor(R.color.text_secondary))
        }

        tabPeople.setOnClickListener {
            tabPeople.setBackgroundResource(R.drawable.bg_segment_selected)
            tabPeople.setTextColor(getColor(R.color.white))
            tabMovies.background = null
            tabMovies.setTextColor(getColor(R.color.text_secondary))
            tabTv.background = null
            tabTv.setTextColor(getColor(R.color.text_secondary))
        }

        val titles = listOf("Dune", "Oppenheimer", "Interstellar", "Inception", "Joker")
        val years = listOf("2021", "2023", "2014", "2010", "2019")
        trending.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        trending.adapter = PosterAdapter(titles, years)

        search.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
        }

        heroAdd.setOnClickListener {
            Toast.makeText(this, "Added to watchlist", Toast.LENGTH_SHORT).show()
        }

        heroTrailer.setOnClickListener {
            Toast.makeText(this, "Trailer clicked", Toast.LENGTH_SHORT).show()
        }

        seeAll.setOnClickListener {
            Toast.makeText(this, "See all clicked", Toast.LENGTH_SHORT).show()
        }

        avatar.setOnClickListener {
            Toast.makeText(this, "Profile clicked", Toast.LENGTH_SHORT).show()
        }
    }
}

class PosterAdapter(val titles: List<String>, val years: List<String>) :
    RecyclerView.Adapter<PosterAdapter.MyHolder>() {

    class MyHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title = view.findViewById<TextView>(R.id.tvTitle)
        val sub = view.findViewById<TextView>(R.id.tvSub)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_poster_h, parent, false)
        return MyHolder(view)
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.title.text = titles[position]
        holder.sub.text = years[position]
    }

    override fun getItemCount(): Int {
        return titles.size
    }
}
