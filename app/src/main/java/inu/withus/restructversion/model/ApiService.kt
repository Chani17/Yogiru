package inu.withus.restructversion.model

import retrofit2.Call
import retrofit2.http.GET

interface ApiService {

    @GET("recipe")
    fun getRecipeTicker(

    ): Call<Ticker>
}