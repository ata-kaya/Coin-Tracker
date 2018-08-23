package com.omidzamani.coinTracker.model

import org.json.JSONException
import org.json.JSONObject
import java.io.Serializable

/**
 * Created by omidzamani on 15.07.2018.
 *
 * this is my coin model
 */
class Coin
/*
"id": "cardano",
"name": "Cardano",
"symbol": "ADA",
"rank": "6",
"price_usd": "0.49356",
"price_btc": "0.00002554",
"24h_volume_usd": "861330000.0",
"market_cap_usd": "12796564935.0",
"available_supply": "25927070538.0",
"total_supply": "31112483745.0",
"max_supply": "45000000000.0",
"percent_change_1h": "-1.76",
"percent_change_24h": "76.62",
"percent_change_7d": "345.02",
"last_updated": "1513528799"
*/
@Throws(JSONException::class)
constructor(item: JSONObject) : Serializable {
    var coinName: String? = null
        private set
    var coinPrice: Double? = null
        private set
    var coinPercent: String? = null
        private set
    var coinPriceBTC: String? = null
        private set
    var coinSymbol: String? = null
        private set
    var coinPercent24h: String? = null
        private set
    var coinPercent7d: String? = null
        private set


    init {

        coinName = item.getString("name")
        coinPercent = item.getString("percent_change_1h")
        coinPrice = item.getDouble("price_usd")
        coinSymbol = item.getString("symbol")
        coinPercent7d = item.getString("percent_change_7d")
        coinPercent24h = item.getString("percent_change_24h")
        coinPriceBTC = item.getString("price_btc")

    }


}
