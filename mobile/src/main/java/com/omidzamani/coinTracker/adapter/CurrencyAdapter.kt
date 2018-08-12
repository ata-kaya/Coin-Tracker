package com.omidzamani.coinTracker.adapter

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import com.omidzamani.coinTracker.R
import kotlinx.android.synthetic.main.currency_list.view.*
import com.omidzamani.coinTracker.interfaces.CustomCurrencyListener
import com.omidzamani.coinTracker.model.Currency
import com.omidzamani.coinTracker.utils.CURRENCY_ALLOWED_SIZE
import com.omidzamani.coinTracker.utils.SharedPreference
import org.jetbrains.anko.longToast
import java.util.*


/**
 * Created by omidzamani on 15.07.2018.
 * this is my adapter for listing currencies
 */
class CurrencyAdapter constructor(private val listener: CustomCurrencyListener,
                                  private val context: Context,
                                  private var items: ArrayList<Currency>,
                                  private val isEditMode: Boolean)
    : RecyclerView.Adapter<CurrencyAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView: View =
                LayoutInflater.from(parent.context)
                        .inflate(R.layout.currency_list, parent, false)

        return MyViewHolder(itemView)

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = items[position]
        holder.title.text = item.currencyFullName
        holder.sell.text = context.getString(R.string.sell).plus(String.format("%.2f", item.currencyPriceSell).plus("₺"))
        holder.buy.text = context.getString(R.string.buy).plus(String.format("%.2f", item.currencyPriceBuy).plus("₺"))
        holder.percent.text = String.format("%.2f", item.currencyPercent).plus("%")
        if (item.currencyPercent!! >= 0.0)
            holder.percent.setTextColor(ContextCompat.getColor(context, R.color.green))
        else
            holder.percent.setTextColor(ContextCompat.getColor(context, R.color.red))

        if (isEditMode) {
            holder.checkBox.visibility = View.VISIBLE
            holder.checkBox.isChecked = SharedPreference.getInstance(context).getCustomCurrencies().contains(item.currencySymbol)
            holder.checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    if (!SharedPreference.getInstance(context).canAddCustomCurrency()) {
                        buttonView.isChecked = false
                        context.longToast(String.format(context.getString(R.string.toast_message_currency_limit), CURRENCY_ALLOWED_SIZE))
                    } else {
                        SharedPreference.getInstance(context).addCurrency(item.currencySymbol as String)
                    }
                } else {
                    SharedPreference.getInstance(context).deleteCurrency(item.currencySymbol as String)
                }
                listener.onCurrencyAddOrRemove()
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
        var buy: TextView = view.buy
        var sell: TextView = view.sell
        var percent: TextView = view.percent
        var checkBox: CheckBox = view.check

    }


}