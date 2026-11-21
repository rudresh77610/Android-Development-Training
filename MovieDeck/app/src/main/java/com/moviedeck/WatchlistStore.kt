package com.moviedeck

import android.content.Context
import com.google.gson.Gson

object WatchlistStore {

    const val MAX = 10

    fun getAll(context: Context): MutableList<WatchItem> {
        val prefs = context.getSharedPreferences("user", Context.MODE_PRIVATE)
        val json = prefs.getString("watchlist", "") ?: ""
        if (json.isEmpty()) {
            return mutableListOf()
        }
        val array = Gson().fromJson(json, Array<WatchItem>::class.java)
        return array.toMutableList()
    }

    fun saveAll(context: Context, list: List<WatchItem>) {
        val prefs = context.getSharedPreferences("user", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putString("watchlist", Gson().toJson(list))
        editor.apply()
    }

    fun add(context: Context, item: WatchItem): String {
        val list = getAll(context)

        for (saved in list) {
            if (saved.id == item.id && saved.type == item.type) {
                return "Already in your watchlist"
            }
        }

        if (list.size >= MAX) {
            return "Watchlist is full (max " + MAX + ")"
        }

        list.add(item)
        saveAll(context, list)
        return "Added to watchlist"
    }

    fun remove(context: Context, item: WatchItem) {
        val list = getAll(context)
        list.removeAll { it.id == item.id && it.type == item.type }
        saveAll(context, list)
    }
}
