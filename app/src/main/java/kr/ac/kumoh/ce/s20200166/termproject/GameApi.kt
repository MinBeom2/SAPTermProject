package kr.ac.kumoh.ce.s20200166.termproject

import retrofit2.http.GET
import retrofit2.http.Query

interface GameApi {
    @GET("game")
    suspend fun getGameList(
        @Query("apikey") apiKey: String = GameApiConfig.apiKey
    ): List<Game>

    @GET("game_info")
    suspend fun getGameInfo(
        @Query("apikey") apiKey: String = GameApiConfig.apiKey,
    ): List<GameInfo>

    @GET("game_image")
    suspend fun getGameImage(
        @Query("apikey") apiKey: String = GameApiConfig.apiKey,
    ): List<GameImage>
}