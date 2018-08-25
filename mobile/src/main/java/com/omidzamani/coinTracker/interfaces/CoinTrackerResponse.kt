package com.omidzamani.coinTracker.interfaces

import com.omidzamani.coinTracker.model.CoinTrack

interface CoinTrackerResponse {
    fun onResponse(coinTrack: CoinTrack)
    fun onFail (responseCode: Int)
}