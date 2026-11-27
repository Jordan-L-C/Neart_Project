package com.example.neart_project.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

/**
 * Quote - one quote from the ZenQuotes API.
 *
 * The API sends back JSON like: [{"q": "The quote text", "a": "The author", ...}]
 * Gson fills in these properties by matching the JSON names, so they must be
 * called q and a. Any other fields in the JSON are ignored.
 *
 * Used by LiftViewModel (gets the quote) and the Stats screen (shows it).
 */
data class Quote(
    val q: String,   // the quote text
    val a: String    // the author
)

/**
 * QuoteApi - gets the quote of the day from the internet using Retrofit.
 *
 * It works like LiftDao but for a website instead of the database:
 * we write the function and the web address, and Retrofit writes the code
 * that makes the request and turns the JSON reply into Quote objects.
 *
 * LiftViewModel calls QuoteApi.service.getTodaysQuote().
 */
interface QuoteApi {

    // Gets https://zenquotes.io/api/today
    // The API always sends back a list, even though it only has one quote in it.
    // "suspend" because it waits for the internet, so it must run in the background
    @GET("api/today")
    suspend fun getTodaysQuote(): List<Quote>

    companion object {
        // The one Retrofit service for the app, created the first time it's used (by lazy)
        val service: QuoteApi by lazy {
            Retrofit.Builder()
                .baseUrl("https://zenquotes.io/")                     // the website
                .addConverterFactory(GsonConverterFactory.create())   // turns JSON into Quote objects
                .build()
                .create(QuoteApi::class.java)
        }
    }
}
