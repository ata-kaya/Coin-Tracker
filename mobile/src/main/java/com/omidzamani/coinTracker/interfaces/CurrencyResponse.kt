package com.omidzamani.coinTracker.interfaces

import com.omidzamani.coinTracker.model.Currency

interface CurrencyResponse {
    fun onResponse(currencies: ArrayList<Currency>)
    fun onFail (responseCode: Int)
}