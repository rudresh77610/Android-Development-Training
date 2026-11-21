package com.moviedeck.api

import com.google.gson.annotations.SerializedName

data class PagedResponse<T>(val results: List<T> = emptyList())

data class MediaItem(
    val id: Int,
    val title: String? = null,
    val name: String? = null,
    val overview: String? = null,
    @SerializedName("poster_path") val posterPath: String? = null,
    @SerializedName("backdrop_path") val backdropPath: String? = null,
    @SerializedName("profile_path") val profilePath: String? = null,
    @SerializedName("release_date") val releaseDate: String? = null,
    @SerializedName("first_air_date") val firstAirDate: String? = null,
    @SerializedName("vote_average") val voteAverage: Double? = null,
    @SerializedName("media_type") val mediaType: String? = null,
    @SerializedName("genre_ids") val genreIds: List<Int>? = null,
    @SerializedName("known_for_department") val knownForDepartment: String? = null,
    val popularity: Double? = null
) {
    val displayTitle: String get() = title ?: name ?: ""
    val year: String get() = (releaseDate ?: firstAirDate)?.take(4) ?: ""
    val isPerson: Boolean get() = mediaType == "person"
}

data class Genre(val id: Int, val name: String)

data class DetailDto(
    val id: Int,
    val title: String? = null,
    val name: String? = null,
    val overview: String? = null,
    val tagline: String? = null,
    @SerializedName("poster_path") val posterPath: String? = null,
    @SerializedName("backdrop_path") val backdropPath: String? = null,
    @SerializedName("release_date") val releaseDate: String? = null,
    @SerializedName("first_air_date") val firstAirDate: String? = null,
    val runtime: Int? = null,
    @SerializedName("episode_run_time") val episodeRunTime: List<Int>? = null,
    @SerializedName("vote_average") val voteAverage: Double? = null,
    @SerializedName("vote_count") val voteCount: Int? = null,
    val genres: List<Genre>? = null,
    @SerializedName("number_of_seasons") val numberOfSeasons: Int? = null
)

data class CreditsDto(val cast: List<CastDto> = emptyList())

data class CastDto(
    val id: Int,
    val name: String? = null,
    val character: String? = null,
    @SerializedName("profile_path") val profilePath: String? = null
)

data class VideosDto(val results: List<VideoDto> = emptyList())

data class VideoDto(val key: String? = null, val site: String? = null, val type: String? = null)

data class PersonDto(
    val id: Int,
    val name: String? = null,
    val biography: String? = null,
    val birthday: String? = null,
    @SerializedName("place_of_birth") val placeOfBirth: String? = null,
    @SerializedName("known_for_department") val knownForDepartment: String? = null,
    @SerializedName("profile_path") val profilePath: String? = null,
    val popularity: Double? = null
)

data class PersonCreditsDto(val cast: List<MediaItem> = emptyList())
