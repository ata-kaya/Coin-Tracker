package com.omidzamani.coinTracker

import com.omidzamani.coinTracker.model.Coin
import com.omidzamani.coinTracker.interfaces.CoinResponse
import com.omidzamani.coinTracker.model.Currency
import com.omidzamani.coinTracker.interfaces.CurrencyResponse
import okhttp3.*
import org.json.JSONArray
import java.io.IOException

/**
 * Created by omidzamani on 15.07.2018.
 */

class API private constructor() {
    private val client: OkHttpClient = OkHttpClient()

    private fun run(url: String, callback: Callback) {
        val request = Request.Builder()
                .url(url)
                .build()

        client.newCall(request).enqueue(callback)
    }


    fun getCoins(callback: CoinResponse) {
        instance.run(BuildConfig.coin_api, object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback.onFail(0)
            }
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val list: ArrayList<Coin> = ArrayList()
                    val res: String = response.body()!!.string()
                    val array = JSONArray(res)
                    (0 until array.length()).mapTo(list) {
                        Coin(array.optJSONObject(it))
                    }
                    callback.onResponse(list)
                } else {
                    callback.onFail(response.code())
                }

            }
        })
    }

    fun getCurrencies(callback: CurrencyResponse) {
        instance.run(BuildConfig.currency_api, object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback.onFail(0)
            }
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val list: ArrayList<Currency> = ArrayList()
                    val res: String = response.body()!!.string()
                    val array = JSONArray(res)
                    (0 until array.length()).mapTo(list) {
                        Currency(array.optJSONObject(it))
                    }
                    callback.onResponse(list)
                } else {
                    callback.onFail(response.code())
                }

            }
        })
    }

    companion object {


        private var api: API? = null

        internal val instance: API
            get() {
                if (api == null)
                    api = API()
                return api as API
            }
    }


}
