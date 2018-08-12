package com.omidzamani.coinTracker.interfaces

import com.omidzamani.coinTracker.model.Coin

interface CoinResponse {

    fun onResponse(coins: ArrayList<Coin>)
    fun onFail (responseCode: Int)
}