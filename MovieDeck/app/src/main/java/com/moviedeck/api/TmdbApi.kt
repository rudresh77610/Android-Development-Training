package com.moviedeck.api

import com.moviedeck.BuildConfig
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbApi {

    @GET("trending/{type}/week")
    suspend fun trending(
        @Path("type") type: String,
        @Query("api_key") apiKey: String = BuildConfig.TMDB_API_KEY
    ): PagedResponse<MediaItem>

    @GET("discover/{type}")
    suspend fun discover(
        @Path("type") type: String,
        @Query("with_genres") genres: String,
        @Query("api_key") apiKey: String = BuildConfig.TMDB_API_KEY
    ): PagedResponse<MediaItem>

    @GET("person/popular")
    suspend fun popularPeople(
        @Query("api_key") apiKey: String = BuildConfig.TMDB_API_KEY
    ): PagedResponse<MediaItem>

    @GET("search/multi")
    suspend fun search(
        @Query("query") query: String,
        @Query("api_key") apiKey: String = BuildConfig.TMDB_API_KEY
    ): PagedResponse<MediaItem>

    @GET("{type}/{id}")
    suspend fun details(
        @Path("type") type: String,
        @Path("id") id: Int,
        @Query("api_key") apiKey: String = BuildConfig.TMDB_API_KEY
    ): DetailDto

    @GET("{type}/{id}/credits")
    suspend fun credits(
        @Path("type") type: String,
        @Path("id") id: Int,
        @Query("api_key") apiKey: String = BuildConfig.TMDB_API_KEY
    ): CreditsDto

    @GET("{type}/{id}/videos")
    suspend fun videos(
        @Path("type") type: String,
        @Path("id") id: Int,
        @Query("api_key") apiKey: String = BuildConfig.TMDB_API_KEY
    ): VideosDto

    @GET("person/{id}")
    suspend fun person(
        @Path("id") id: Int,
        @Query("api_key") apiKey: String = BuildConfig.TMDB_API_KEY
    ): PersonDto

    @GET("person/{id}/combined_credits")
    suspend fun personCredits(
        @Path("id") id: Int,
        @Query("api_key") apiKey: String = BuildConfig.TMDB_API_KEY
    ): PersonCreditsDto
}
