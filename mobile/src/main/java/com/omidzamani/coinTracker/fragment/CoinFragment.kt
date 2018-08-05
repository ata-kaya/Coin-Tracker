package com.omidzamani.coinTracker.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import android.widget.Toast
import com.omidzamani.coinTracker.R
import com.omidzamani.coinTracker.adapter.CoinAdapter
import com.omidzamani.coinTracker.interfaces.CustomCoinsListener
import com.omidzamani.coinTracker.model.Coin
import com.omidzamani.coinTracker.utils.SharedPreference
import kotlinx.android.synthetic.main.fragment_coin.*
import kotlinx.android.synthetic.main.fragment_coin.view.*
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONArray
import java.io.IOException

/**
 * Created by omidzamani on 24.07.2018.
 * A fragment containing coins view.
 */
class CoinFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener, CustomCoinsListener {


    private lateinit var pref: SharedPreference
    private lateinit var rootView:View;
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_coin, container, false)
        pref = SharedPreference.getInstance(context)
        rootView.swipe_refresh_layout.setOnRefreshListener(this)
        api(false)
        rootView.add_button.setOnClickListener{
            if (!pref.canAddCustomCoin()){
                pref.addCoins()
                api(false)
            } else {
                Toast.makeText(context,"You should select 4 coin", Toast.LENGTH_LONG).show()
            }
        }
        setHasOptionsMenu(true)
        return rootView
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
        fun newInstance(sectionNumber: Int): CoinFragment {
            val fragment = CoinFragment()
            val args = Bundle()
            args.putInt(ARG_SECTION_NUMBER, sectionNumber)
            fragment.arguments = args
            return fragment
        }
    }




    override fun onCreateOptionsMenu(menu: Menu?, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.main, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item!!.itemId == R.id.edit_menu) {
            api(true)
//            startActivity(Intent(this, MainActivity::class.java))
        } else if (item.itemId == R.id.delete_manu) {
            SharedPreference.getInstance(context).deleteAllCoins()
        }
        return true
    }

    override fun onRefresh() {
        api(false)
    }


    override fun onCoinAddOrRemove() {
        if (pref.tempCoins.size == 6){
            setButtonActive(true)
        } else {
            setButtonActive(false)
        }
    }

    private fun setButtonActive(visibility: Boolean) {

//        add_button.setBackgroundColor(ContextCompat.getColor(applicationContext, common_google_signin_btn_text_light_default))
////        add_button.setBackgroundColor(ContextCompat.getColor(applicationContext, Holo_Green_Dark))
    }


    private fun api(isEditMode : Boolean) {

        val visibility = if (isEditMode)
            View.VISIBLE
        else
            View.GONE

        rootView.add_button.visibility = visibility
        API.instance.run(getString(R.string.coin_api), object : Callback {
            override fun onFailure(call: Call, e: IOException) {}
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val list : ArrayList<Coin> = ArrayList()
                    val res: String = response.body()!!.string()
                    val array = JSONArray(res)
                    (0 until array.length()).mapTo(list) {
                        Coin(array.optJSONObject(it))
                    }
                    Handler(Looper.getMainLooper()).post {
                        swipe_refresh_layout.isRefreshing = false
                        coin_list.adapter = CoinAdapter(this@CoinFragment, context, list, isEditMode)
                        coin_list.layoutManager = LinearLayoutManager(context)
                    }
                }

            }
        })
    }



}