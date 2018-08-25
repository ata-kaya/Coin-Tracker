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
import com.omidzamani.coinTracker.adapter.CoinAdapter
import com.omidzamani.coinTracker.interfaces.CustomCoinsListener
import com.omidzamani.coinTracker.model.Coin
import com.omidzamani.coinTracker.interfaces.CoinResponse
import com.omidzamani.coinTracker.utils.SharedPreference
import kotlinx.android.synthetic.main.fragment_coin.*
import kotlinx.android.synthetic.main.fragment_coin.view.*
import org.jetbrains.anko.support.v4.longToast

/**
 * Created by omidzamani on 24.07.2018.
 * A fragment containing coins view.
 */
class CoinFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener, CustomCoinsListener {


    private lateinit var pref: SharedPreference
    private lateinit var rootView: View
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_coin, container, false)
        pref = SharedPreference.getInstance(context)
        rootView.swipe_refresh_layout.setOnRefreshListener(this)
        api()
        return rootView
    }

    companion object {

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        fun newInstance(): CoinFragment {
            return CoinFragment()
        }
    }




    override fun onRefresh() {
        api()
    }


    override fun onCoinAddOrRemove() {
    }


    private fun api() {
        API.instance.getCoins(object: CoinResponse {
            override fun onFail(responseCode: Int) {
                Handler(Looper.getMainLooper()).post{
                    longToast(R.string.some_wrong)
                }
            }

            override fun onResponse(coins: ArrayList<Coin>) {
                Handler(Looper.getMainLooper()).post {
                    swipe_refresh_layout.isRefreshing = false
                    coin_list.adapter = CoinAdapter(this@CoinFragment, context, coins, false)
                    coin_list.layoutManager = LinearLayoutManager(context)
                }
            }

        })
    }


}