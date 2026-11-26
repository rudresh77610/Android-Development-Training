package com.moviedeck

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2

class OnboardingOneActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)
        BottomNav.attach(this)

        val pager = findViewById<ViewPager2>(R.id.pager)

        val pages = listOf(
            R.layout.activity_onboarding_1,
            R.layout.activity_onboarding_2,
            R.layout.activity_onboarding_3
        )

        val skip = findViewById<TextView>(R.id.skip)

        skip.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            finish()
        }

        pager.adapter = OnboardingAdapter(pages) {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}

class OnboardingAdapter(
    private val pages: List<Int>,
    private val onLastNext: () -> Unit
) : RecyclerView.Adapter<OnboardingAdapter.PageHolder>() {

    class PageHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun getItemViewType(position: Int): Int {
        return pages[position]
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return PageHolder(view)
    }

    override fun onBindViewHolder(holder: PageHolder, position: Int) {
        val next = holder.itemView.findViewById<ImageView>(R.id.next)

        if (position == pages.size - 1) {
            next.visibility = View.VISIBLE
            next.setOnClickListener {
                onLastNext()
            }
        } else {
            next.visibility = View.INVISIBLE
        }
    }

    override fun getItemCount(): Int {
        return pages.size
    }
}
