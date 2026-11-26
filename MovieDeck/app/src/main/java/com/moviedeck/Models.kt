package com.moviedeck

data class ProfileInfo(
    val initials: String,
    val name: String,
    val email: String,
    val watchCount: String,
    val searchCount: String
)

data class WatchItem(
    val id: Int,
    val type: String,
    val title: String,
    val posterPath: String?,
    val year: String
)

data class HomeInfo(
    val date: String,
    val greeting: String,
    val initials: String
)

data class HeroInfo(
    val badge: String,
    val title: String,
    val meta: String,
    val section: String
)

data class MovieInfo(
    val genres: String,
    val title: String,
    val meta: String,
    val rating: String,
    val ratingCaption: String,
    val runtime: String,
    val runtimeCaption: String,
    val release: String,
    val releaseCaption: String,
    val synopsis: String
)

data class ArtistInfo(
    val initials: String,
    val role: String,
    val name: String,
    val popularity: String,
    val credits: String,
    val born: String,
    val place: String,
    val known: String,
    val creditsCount: String,
    val bio: String,
    val titles: String
)
