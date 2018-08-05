package com.omidzamani.coinTracker.utils

import android.content.Context
import android.content.SharedPreferences

/**
 * Created by omidzamani on 15.07.2018.
 */


const val CURRENCY_ALLOWED_SIZE = 4
const val COIN_ALLOWED_SIZE = 4


class SharedPreference private constructor(val context: Context) {


    var tempCoins: ArrayList<String> = ArrayList()
    var tempCurrencies: ArrayList<String> = ArrayList()

    init {
        tempCoins = getCustomCoins()
        tempCurrencies = getCustomCurrencies()
    }

    fun hasCustomCoin(): Boolean {
        return tempCoins.size > 0
    }


    fun hasCustomCurrency(): Boolean {
        return tempCurrencies.size > 0
    }


    fun canAddCustomCoin(): Boolean {
        return tempCoins.size < COIN_ALLOWED_SIZE
    }

    fun canAddCustomCurrency(): Boolean {
        return tempCurrencies.size < CURRENCY_ALLOWED_SIZE
    }


    companion object {


        private var instance: SharedPreference? = null

        fun getInstance(context: Context): SharedPreference {
            if (instance == null)  // NOT thread safe!
                instance = SharedPreference(context)

            return instance as SharedPreference
        }


    }

    fun getCustomCoins(): ArrayList<String> {
        return getCustomItemsByKey("coin")
    }

    fun getCustomCurrencies(): ArrayList<String> {
        return getCustomItemsByKey("currency")
    }


    private fun getCustomItemsByKey(key: String): ArrayList<String> {
        val items = getSharedPreference().getString(key, null)
        val arrayList: ArrayList<String> = ArrayList()
        if (items != null)
            arrayList.addAll(items.split(";"))
        return arrayList
    }

    private fun getSharedPreference(): SharedPreferences {
        return context.getSharedPreferences("Coins", Context.MODE_PRIVATE)
    }

    fun deleteAllCoins() {

        deleteAllItems("coin")
    }

    fun deleteAllCurrencies() {
        deleteAllItems("currency")
    }

    private fun deleteAllItems(key: String) {
        val editor: SharedPreferences.Editor = getSharedPreference().edit()
        editor.putString(key, null)
        editor.apply()
    }

    fun deleteCoin(coin: String) {
        tempCoins.remove(coin)
    }

    fun deleteCurrency(currency: String) {
        tempCurrencies.remove(currency)
    }


    fun addCoin(coin: String) {
        if (canAddCustomCoin()) {
            tempCoins.add(coin)
        }
    }

    fun addCurrency(currency: String) {
        if (canAddCustomCurrency()) {
            tempCurrencies.add(currency)
        }
    }

    fun addCoins() {
        addItems("coin", tempCoins)
    }

    fun addCurrencies() {
        addItems("currency", tempCurrencies)
    }

    private fun addItems(key: String, data: ArrayList<String>) {
        val editor: SharedPreferences.Editor = getSharedPreference().edit()
        var items = ""
        for (item in data) {
            items = items.plus(item)
            items = items.plus(";")
        }
        items = items.substring(0, items.length - 1)
        editor.putString(key, items)
        editor.apply()
        if (key == "coin")
            tempCoins = getCustomCoins()
        else if (key == "currency")
            tempCurrencies = getCustomCurrencies()
    }


}