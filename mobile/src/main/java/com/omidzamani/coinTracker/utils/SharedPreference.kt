package com.omidzamani.coinTracker.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences

/**
 * Created by omidzamani on 15.07.2018.
 */


const val CURRENCY_ALLOWED_SIZE = 4
const val COIN_ALLOWED_SIZE = 4

enum class Type{
    COIN,
    CURRENCY
}

class SharedPreference private constructor(val context: Context) {


    private var tempCoins: ArrayList<String> = ArrayList()
    private var tempCurrencies: ArrayList<String> = ArrayList()

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


        @SuppressLint("StaticFieldLeak")
        private var instance: SharedPreference? = null

        fun getInstance(context: Context): SharedPreference {
            if (instance == null)  // NOT thread safe!
                instance = SharedPreference(context)

            return instance as SharedPreference
        }


    }

    fun getCustomCoins(): ArrayList<String> {
        return getCustomItemsByKey(Type.COIN.name.toLowerCase())
    }

    fun getCustomCurrencies(): ArrayList<String> {
        return getCustomItemsByKey(Type.CURRENCY.name.toLowerCase())
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

        deleteAllItems(Type.COIN.name.toLowerCase())
        tempCoins = getCustomCoins()
    }

    fun deleteAllCurrencies() {
        deleteAllItems(Type.CURRENCY.name.toLowerCase())
        tempCurrencies = getCustomCurrencies()
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
        addItems(Type.COIN.name.toLowerCase(), tempCoins)
    }

    fun addCurrencies() {
        addItems(Type.CURRENCY.name.toLowerCase(), tempCurrencies)
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
        if (key == Type.COIN.name.toLowerCase())
            tempCoins = getCustomCoins()
        else if (key == Type.CURRENCY.name.toLowerCase())
            tempCurrencies = getCustomCurrencies()
    }


}