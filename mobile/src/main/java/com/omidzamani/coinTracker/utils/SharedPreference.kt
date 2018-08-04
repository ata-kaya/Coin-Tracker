package com.omidzamani.coinTracker.utils

import android.content.Context
import android.content.SharedPreferences
import com.omidzamani.coinTracker.model.Coin

/**
 * Created by omidzamani on 15.07.2018.
 */
class SharedPreference private constructor(context: Context){


    val context : Context
    var temp_coins:ArrayList<String> = ArrayList()

    init {
        this.context= context
        temp_coins = getCustomCoins()
    }

    fun hasCustomCoin(): Boolean {
        return temp_coins.size > 0
    }


    fun hasCustomCurrency(): Boolean {
        return false
    }


    fun canAddCustomCoin() : Boolean {
        return temp_coins.size < 4
    }


    companion object {


        private var instance : SharedPreference? = null

        fun  getInstance(context: Context): SharedPreference {
            if (instance == null)  // NOT thread safe!
                instance = SharedPreference(context)

            return instance as SharedPreference
        }
    }

    fun getCustomCoins(): ArrayList <String>{
        val coins = getSharedPreference().getString("coin",null)
        val arrayList: ArrayList<String> = ArrayList()
        if (coins != null)
            arrayList.addAll(coins.split(";"))
        return arrayList

    }

    private fun getSharedPreference() : SharedPreferences {
        return context.getSharedPreferences("Coins", Context.MODE_PRIVATE)
    }

    fun deleteAllCoins() {

        val editor: SharedPreferences.Editor = getSharedPreference().edit()
        editor.putString("coin", null)
        editor.apply()
    }

    fun deleteCoin(coin: String) {
        temp_coins.remove(coin)
    }
    fun deleteCoin1(coin: String) {
        val list: ArrayList<String> = getCustomCoins()
        list.remove(coin)
        val editor: SharedPreferences.Editor = getSharedPreference().edit()
        var coins = ""
        for (coinItem in list) {
            coins = coins.plus(coinItem)
            coins = coins.plus(";")
        }
        coins = coins.substring(0, coins.length-1)
        editor.putString("coin", coins)
        editor.apply()
    }

    fun addCoin(coin: String) {
        if (canAddCustomCoin()) {
            temp_coins.add(coin)
        }
    }

    fun addCoins() {
        val editor: SharedPreferences.Editor = getSharedPreference().edit()
        var coins = ""
        for (coinItem in temp_coins) {
            coins = coins.plus(coinItem)
            coins = coins.plus(";")
        }
        coins = coins.substring(0, coins.length-1)
        editor.putString("coin", coins)
        editor.apply()
        temp_coins = getCustomCoins()
    }


    fun addCoin1(coin: String) {
        val list: ArrayList<String> = getCustomCoins()
        list.add(coin)
        val editor: SharedPreferences.Editor = getSharedPreference().edit()
        var coins = ""
        for (coinItem in list) {
            coins = coins.plus(coinItem)
            coins = coins.plus(";")
        }
        coins = coins.substring(0, coins.length-1)
        editor.putString("coin", coins)
        editor.apply()
    }


}