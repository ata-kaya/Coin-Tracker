package com.omidzamani.coinTracker.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.omidzamani.coinTracker.R
import com.omidzamani.coinTracker.adapter.CurrencyAdapter
import com.omidzamani.coinTracker.interfaces.CustomCurrencyListener
import com.omidzamani.coinTracker.model.Currency
import com.omidzamani.coinTracker.utils.SharedPreference
import kotlinx.android.synthetic.main.fragment_currency.*
import kotlinx.android.synthetic.main.fragment_currency.view.*
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONArray
import java.io.IOException

/**
 * Created by omidzamani on 24.07.2018.
 * A fragment containing currency view.
 */
class CurrencyFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener, CustomCurrencyListener {



    private lateinit var pref: SharedPreference
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_currency, container, false)
        pref = SharedPreference.getInstance(context)
        rootView.swipe_refresh_layout.setOnRefreshListener(this)
        api(false)
        rootView.add_button.setOnClickListener{
            if (pref.temp_coins.size == 6){
                pref.addCoins()
                api(false)
            } else {
                Toast.makeText(context,"You should select 6 coin", Toast.LENGTH_LONG).show()
            }
        }
        return rootView
    }



    private fun api(isEditMode : Boolean) {

        API.instance.run(getString(R.string.currency_api), object : Callback {
            override fun onFailure(call: Call, e: IOException) {}
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val list : ArrayList<Currency> = ArrayList()
                    val res: String = response.body()!!.string()
                    val array = JSONArray(res)
                    (0 until array.length()).mapTo(list) {
                        Currency(array.optJSONObject(it))
                    }
                    Handler(Looper.getMainLooper()).post {
                        swipe_refresh_layout.isRefreshing = false
                        currency_list.adapter = CurrencyAdapter(this@CurrencyFragment, context, list, isEditMode)
                        currency_list.layoutManager = LinearLayoutManager(context)
                    }
                }

            }
        })
    }



    override fun onRefresh() {
        api(false)
    }

    override fun onCurrencyAddOrRemove() {
    }


    companion object {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private val ARG_SECTION_NUMBER = "section_number"

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        fun newInstance(sectionNumber: Int): CurrencyFragment {
            val fragment = CurrencyFragment()
            val args = Bundle()
            args.putInt(ARG_SECTION_NUMBER, sectionNumber)
            fragment.arguments = args
            return fragment
        }
    }
}