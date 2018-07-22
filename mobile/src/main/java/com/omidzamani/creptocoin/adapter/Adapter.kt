package com.omidzamani.creptocoin.adapter

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import com.omidzamani.creptocoin.model.Coin
import com.omidzamani.creptocoin.R
import kotlinx.android.synthetic.main.list.view.*
import java.util.ArrayList
import android.widget.CompoundButton
import android.widget.Toast
import com.omidzamani.creptocoin.MainActivity
import com.omidzamani.creptocoin.interfaces.CustomCoinsListener
import com.omidzamani.creptocoin.utils.SharedPreference


/**
 * Created by omidzamani on 15.07.2018.
 * this is my adapter for listing coins
 */
class Adapter constructor(private val listener: CustomCoinsListener,
                          private val context: Context,
                          private var items: ArrayList<Coin>,
                          private val isEditMode: Boolean)
    : RecyclerView.Adapter<Adapter.MyViewHolder>() {





    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView: View =
            LayoutInflater.from(parent.context)
                    .inflate(R.layout.list, parent, false)

        return MyViewHolder(itemView)

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = items[position]
        holder.title.text = item.coinName.plus(", ").plus(item.coinSymbol)
        holder.dollar.text = item.coinPrice.plus(" $")
        holder.btc.text = item.coinPriceBTC.plus(" BTC")
        holder.percent.text = item.coinPercent.plus("%")
        if (java.lang.Float.parseFloat(item.coinPercent) >= 0.0)
            holder.percent.setTextColor(Color.GREEN)
        else
            holder.percent.setTextColor(Color.RED)

        if (isEditMode) {
            holder.checkBox.visibility = View.VISIBLE
            holder.checkBox.isChecked = SharedPreference.getInstance(context).getCustomCoins().contains(item.coinSymbol)
            holder.checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    if (!SharedPreference.getInstance(context).canAddCustomCoin()) {
                        buttonView.isChecked = false
                        Toast.makeText(context, "Limit is 6 coins", Toast.LENGTH_LONG).show()
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

    }


}