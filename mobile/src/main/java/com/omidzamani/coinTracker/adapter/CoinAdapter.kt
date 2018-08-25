package com.omidzamani.coinTracker.adapter

import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import com.omidzamani.coinTracker.model.Coin
import com.omidzamani.coinTracker.R
import com.omidzamani.coinTracker.activity.DetailActivity
import kotlinx.android.synthetic.main.coin_list.view.*
import java.util.ArrayList
import com.omidzamani.coinTracker.interfaces.CustomCoinsListener
import com.omidzamani.coinTracker.utils.COIN_ALLOWED_SIZE
import com.omidzamani.coinTracker.utils.SharedPreference
import org.jetbrains.anko.longToast


/**
 * Created by omidzamani on 15.07.2018.
 * this is my adapter for listing coins
 */
class CoinAdapter constructor(private val listener: CustomCoinsListener,
                              private val context: Context,
                              private var items: ArrayList<Coin>,
                              private val isEditMode: Boolean)
    : RecyclerView.Adapter<CoinAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView: View =
                LayoutInflater.from(parent.context)
                        .inflate(R.layout.coin_list, parent, false)

        return MyViewHolder(itemView)

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = items[position]
        holder.title.text = item.coinName.plus(", ").plus(item.coinSymbol)
        holder.dollar.text = String.format("%.2f", item.coinPrice).plus(" $")
        holder.btc.text = item.coinPriceBTC.plus(" BTC")
        holder.percent.text = item.coinPercent.plus("%")
        if (java.lang.Float.parseFloat(item.coinPercent) >= 0.0)
            holder.percent.setTextColor(ContextCompat.getColor(context, R.color.green))
        else
            holder.percent.setTextColor(ContextCompat.getColor(context, R.color.red))

        if (isEditMode) {
            holder.checkBox.visibility = View.VISIBLE
            holder.checkBox.isChecked = SharedPreference.getInstance(context).getCustomCoins().contains(item.coinSymbol)
            holder.checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    if (!SharedPreference.getInstance(context).canAddCustomCoin()) {
                        buttonView.isChecked = false
                        context.longToast(String.format(context.getString(R.string.toast_message_coin_limit), COIN_ALLOWED_SIZE))
                    } else {
                        SharedPreference.getInstance(context).addCoin(item.coinSymbol as String)
                    }
                } else {
                    SharedPreference.getInstance(context).deleteCoin(item.coinSymbol as String)
                }
                listener.onCoinAddOrRemove()
            }

        } else {
            holder.checkBox.visibility = View.GONE
        }

        holder.rootView.setOnClickListener {
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra("Value", item)
            context.startActivity(intent)
        }
    }


    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class MyViewHolder constructor(view: View) : RecyclerView.ViewHolder(view) {


        var title: TextView = view.title
        var btc: TextView = view.btc
        var dollar: TextView = view.dolar
        var percent: TextView = view.percent
        var checkBox: CheckBox = view.check
        var rootView:LinearLayout = view.root_layout

    }


}