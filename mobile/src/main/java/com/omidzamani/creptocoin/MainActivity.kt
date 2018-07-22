package com.omidzamani.creptocoin

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.omidzamani.creptocoin.adapter.Adapter
import com.omidzamani.creptocoin.interfaces.CustomCoinsListener
import com.omidzamani.creptocoin.model.Coin
import com.omidzamani.creptocoin.utils.SharedPreference
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import org.json.JSONArray
import java.io.IOException


class MainActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener, CustomCoinsListener {


    private lateinit var pref: SharedPreference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        pref = SharedPreference.getInstance(applicationContext)
        swipe_refresh_layout.setOnRefreshListener(this)
        api(false)
        add_button.setOnClickListener{
            if (pref.temp_coins.size == 6){
                pref.addCoins()
                api(false)
            } else {
                Toast.makeText(applicationContext,"You should select 6 coin", Toast.LENGTH_LONG).show()
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        val menuInflater : MenuInflater = menuInflater
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item!!.itemId == R.id.edit_menu) {
            api(true)
        } else if (item.itemId == R.id.delete_manu) {
            SharedPreference.getInstance(applicationContext).deleteAllCoins()
        }
        return true
    }

    override fun onRefresh() {
        api(false)
    }


    override fun onCoinAddOrRemove() {
        if (pref.temp_coins.size == 6){
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

        add_button.visibility = visibility
        API.instance.run("https://api.coinmarketcap.com/v1/ticker/", object : Callback {
            override fun onFailure(call: Call, e: IOException) {}
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val list : ArrayList<Coin> = ArrayList()
                    val res: String = response.body()!!.string()
                    val array = JSONArray(res)
                    for (i in 0 until array.length()){
                        println(i)
                        list.add(Coin(array.optJSONObject(i)))
                    }
                    Handler(Looper.getMainLooper()).post({
                        swipe_refresh_layout.isRefreshing = false
                        coin_list.adapter = Adapter(this@MainActivity, applicationContext, list, isEditMode)
                        coin_list.layoutManager = LinearLayoutManager(applicationContext)
                    })
                }

            }
        })
    }
}
