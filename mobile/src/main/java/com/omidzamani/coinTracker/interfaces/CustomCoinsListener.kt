package com.omidzamani.coinTracker.interfaces

/**
 * Created by omidzamani on 22.07.2018.
 * rhis is custom listener for connecting adapter to the fragment
 */
interface CustomCoinsListener {
    fun onCoinAddOrRemove()
}

interface CustomCurrencyListener {
    fun onCurrencyAddOrRemove()
}