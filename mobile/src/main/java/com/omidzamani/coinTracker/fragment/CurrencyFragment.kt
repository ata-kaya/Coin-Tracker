package com.omidzamani.coinTracker.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import com.omidzamani.coinTracker.API
import com.omidzamani.coinTracker.R
import com.omidzamani.coinTracker.adapter.CurrencyAdapter
import com.omidzamani.coinTracker.interfaces.CustomCurrencyListener
import com.omidzamani.coinTracker.model.Currency
import com.omidzamani.coinTracker.interfaces.CurrencyResponse
import com.omidzamani.coinTracker.utils.SharedPreference
import kotlinx.android.synthetic.main.fragment_currency.*
import kotlinx.android.synthetic.main.fragment_currency.view.*
import org.jetbrains.anko.support.v4.longToast

/**
 * Created by omidzamani on 24.07.2018.
 * A fragment containing currency view.
 */
class CurrencyFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener, CustomCurrencyListener {


    private lateinit var pref: SharedPreference
    private lateinit var rootView: View
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_currency, container, false)
        pref = SharedPreference.getInstance(context)
        rootView.swipe_refresh_layout.setOnRefreshListener(this)
        api()
        return rootView
    }



    private fun api() {

        API.instance.getCurrencies(object : CurrencyResponse {
            override fun onResponse(currencies: ArrayList<Currency>) {
                Handler(Looper.getMainLooper()).post {
                    swipe_refresh_layout.isRefreshing = false
                    currency_list.adapter = CurrencyAdapter(this@CurrencyFragment, context, currencies, false)
                    currency_list.layoutManager = LinearLayoutManager(context)
                }
            }

            override fun onFail(responseCode: Int) {
                Handler(Looper.getMainLooper()).post {
                    longToast(R.string.some_wrong)
                }
            }

        })
    }


    override fun onRefresh() {
        api()
    }

    override fun onCurrencyAddOrRemove() {
    }


    companion object {
        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        fun newInstance(): CurrencyFragment {
            return CurrencyFragment()
        }
    }
}