package kr.ac.kumoh.ce.s20200166.termproject

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class GameViewModel: ViewModel() {

    private val gameApi: GameApi
    private val _gameList = MutableLiveData<List<Game>>()
    val gameList: LiveData<List<Game>>
        get() = _gameList

    private val _gameInfoList = MutableLiveData<List<GameInfo>>()
    val gameInfoList: LiveData<List<GameInfo>>
        get() = _gameInfoList

    private val _gameImageList = MutableLiveData<List<GameImage>>()
    val gameImageList: LiveData<List<GameImage>>
        get() = _gameImageList


    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(GameApiConfig.SERVER_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        gameApi = retrofit.create(GameApi::class.java)
        fetchGames()
        fetchInfo()
        fetchImage()
    }

    private fun fetchGames() {
        viewModelScope.launch {
            try {
                val response = gameApi.getGameList()
                _gameList.value = response
            } catch (e: Exception) {
                Log.e("fetchGames", "Error fetching game list: ${e.message}")
            }
        }
    }

    private fun fetchInfo() {
        viewModelScope.launch {
            try {
                val response = gameApi.getGameInfo()
                _gameInfoList.value = response
            } catch (e: Exception) {
                Log.e("fetchGames", "Error fetching game list: ${e.message}")
            }
        }
    }

    private fun fetchImage() {
        viewModelScope.launch {
            try {
                val response = gameApi.getGameImage()
                _gameImageList.value = response
            } catch (e: Exception) {
                Log.e("fetchGames", "Error fetching game list: ${e.message}")
            }
        }
    }
}