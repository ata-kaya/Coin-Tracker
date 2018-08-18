package com.omidzamani.coinTracker.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v7.widget.LinearLayoutManager
import com.omidzamani.coinTracker.API
import com.omidzamani.coinTracker.R
import com.omidzamani.coinTracker.adapter.CoinAdapter
import com.omidzamani.coinTracker.adapter.CurrencyAdapter
import com.omidzamani.coinTracker.interfaces.CustomCoinsListener
import com.omidzamani.coinTracker.interfaces.CustomCurrencyListener
import com.omidzamani.coinTracker.model.Coin
import com.omidzamani.coinTracker.interfaces.CoinResponse
import com.omidzamani.coinTracker.model.Currency
import com.omidzamani.coinTracker.interfaces.CurrencyResponse
import com.omidzamani.coinTracker.utils.COIN_ALLOWED_SIZE
import com.omidzamani.coinTracker.utils.CURRENCY_ALLOWED_SIZE
import com.omidzamani.coinTracker.utils.SharedPreference
import kotlinx.android.synthetic.main.activity_list.*
import com.omidzamani.coinTracker.utils.Type
import org.jetbrains.anko.*


class ListActivity : AppCompatActivity(), CustomCoinsListener, CustomCurrencyListener {

    lateinit var type: Type
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        if (intent.hasExtra("itemName")) {

            type = if (intent.getStringExtra("itemName") == Type.COIN.name.toLowerCase()) {
                getCoins()
                Type.COIN
            } else {
                getCurrencies()
                Type.CURRENCY
            }
        }

        val pref = SharedPreference.getInstance(this)
        add_button.setOnClickListener {
            if (type == Type.COIN)
                addCoins(pref)
            else
                addCurrencies(pref)
        }


    }

    private fun addCoins(pref: SharedPreference) {
        add_button.setOnClickListener {
            if (!pref.canAddCustomCoin()) {
                pref.addCoins()
                showAlert()
            } else {
                longToast(String.format(getString(R.string.toast_message_2), COIN_ALLOWED_SIZE))
            }
        }
    }

    private fun addCurrencies(pref: SharedPreference) {
        if (!pref.canAddCustomCurrency()) {
            pref.addCurrencies()
            showAlert()
        } else {
            longToast(String.format(getString(R.string.toast_message_1), CURRENCY_ALLOWED_SIZE))
        }
    }

    private fun showAlert() {
        alert(String.format(getString(R.string.items_added), type.name.toLowerCase()), getString(android.R.string.dialog_alert_title)) {
            okButton {

                finish()
            }
        }.show()
    }


    private fun getCurrencies() {
        API.instance.getCurrencies(object : CurrencyResponse {
            override fun onResponse(currencies: ArrayList<Currency>) {
                Handler(Looper.getMainLooper()).post {
                    list.adapter = CurrencyAdapter(this@ListActivity, this@ListActivity, currencies, true)
                    list.layoutManager = LinearLayoutManager(this@ListActivity)
                }
            }

            override fun onFail(responseCode: Int) {
                Handler(Looper.getMainLooper()).post {
                    longToast(R.string.some_wrong)
                }
            }

        })
    }

    private fun getCoins() {
        API.instance.getCoins(object : CoinResponse {
            override fun onResponse(coins: ArrayList<Coin>) {
                Handler(Looper.getMainLooper()).post {
                    list.adapter = CoinAdapter(this@ListActivity, this@ListActivity, coins, true)
                    list.layoutManager = LinearLayoutManager(this@ListActivity)
                }
            }

            override fun onFail(responseCode: Int) {
                Handler(Looper.getMainLooper()).post{
                    longToast(R.string.some_wrong)
                }
            }

        })
    }


    override fun onCoinAddOrRemove() {
    }

    override fun onCurrencyAddOrRemove() {
    }


}
