package kr.ac.kumoh.ce.s20200166.termproject

data class GameInfo(
    val info_id: Int,
    val game_id: Int,
    val price: Int,
    val rating: Int,
    val release_year: Int,
    val developer: String
)