package com.moviedeck

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout

object BottomNav {

    fun attach(activity: Activity) {
        val content = activity.findViewById<ViewGroup>(android.R.id.content)
        val screen = content.getChildAt(0)
        content.removeView(screen)

        val wrapper = LinearLayout(activity)
        wrapper.orientation = LinearLayout.VERTICAL

        screen.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f
        )
        wrapper.addView(screen)

        val bar = LayoutInflater.from(activity).inflate(R.layout.view_bottom_nav, wrapper, false)
        wrapper.addView(bar)

        content.addView(
            wrapper,
            ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        )

        bar.findViewById<ViewGroup>(R.id.navBack).setOnClickListener {
            activity.finish()
        }

        bar.findViewById<ViewGroup>(R.id.navHome).setOnClickListener {
            val intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_HOME)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            activity.startActivity(intent)
        }

        bar.findViewById<ViewGroup>(R.id.navQuit).setOnClickListener {
            activity.finishAffinity()
        }
    }
}
