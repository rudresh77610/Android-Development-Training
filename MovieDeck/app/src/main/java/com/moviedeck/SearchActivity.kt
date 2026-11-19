package com.moviedeck

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SearchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val searchBox = findViewById<EditText>(R.id.etSearch)
        val clear = findViewById<View>(R.id.btnClear)
        val chips = findViewById<LinearLayout>(R.id.chips)
        val resultsLabel = findViewById<TextView>(R.id.tvResultsLabel)
        val message = findViewById<TextView>(R.id.tvMessage)
        val results = findViewById<RecyclerView>(R.id.rvResults)
        val recents = findViewById<LinearLayout>(R.id.recents)

        val names = listOf("All", "Movies", "TV", "People")
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

        val recentList = listOf("Batman", "Avatar", "Tom Hanks")
        for (text in recentList) {
            val row = LayoutInflater.from(this).inflate(R.layout.item_recent, recents, false)
            val query = row.findViewById<TextView>(R.id.tvQuery)
            val remove = row.findViewById<View>(R.id.ivRemove)
            query.text = text
            row.setOnClickListener {
                searchBox.setText(text)
            }
            remove.setOnClickListener {
                recents.removeView(row)
            }
            recents.addView(row)
        }

        val titles = listOf("Batman Begins", "The Dark Knight", "Batman Returns", "Batman Forever")
        val metas = listOf("Movie · 2005", "Movie · 2008", "Movie · 1992", "Movie · 1995")
        val ratings = listOf("8.2", "9.0", "7.1", "5.4")
        results.layoutManager = LinearLayoutManager(this)
        results.adapter = SearchAdapter(titles, metas, ratings)

        searchBox.setOnEditorActionListener { _, _, _ ->
            val text = searchBox.text.toString()
            if (text.length < 2) {
                Toast.makeText(this, "Type at least 2 letters", Toast.LENGTH_SHORT).show()
            } else {
                resultsLabel.text = "4 results for \"" + text + "\""
                resultsLabel.visibility = View.VISIBLE
                message.visibility = View.GONE
            }
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
            resultsLabel.visibility = View.GONE
        }
    }
}

class SearchAdapter(val titles: List<String>, val metas: List<String>, val ratings: List<String>) :
    RecyclerView.Adapter<SearchAdapter.MyHolder>() {

    class MyHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title = view.findViewById<TextView>(R.id.tvTitle)
        val meta = view.findViewById<TextView>(R.id.tvMeta)
        val rating = view.findViewById<TextView>(R.id.tvRating)
        val add = view.findViewById<View>(R.id.btnAdd)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder
    {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_search_result, parent, false)
        return MyHolder(view)
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.title.text = titles[position]
        holder.meta.text = metas[position]
        holder.rating.text = ratings[position]
        holder.add.setOnClickListener {
            Toast.makeText(holder.itemView.context, "Added to watchlist", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int {
        return titles.size
    }
}
