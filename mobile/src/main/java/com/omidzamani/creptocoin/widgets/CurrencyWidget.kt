package com.omidzamani.creptocoin.widgets

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.util.Log
import android.widget.RemoteViews

import com.omidzamani.creptocoin.R
import com.omidzamani.creptocoin.model.Currency
import com.omidzamani.creptocoin.utils.SharedPreference
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONArray
import java.io.IOException
import java.text.DecimalFormat

/**
 * Implementation of App Widget functionality.
 */
class CurrencyWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            Log.d("OMID", "currency ids="+appWidgetId)
            updateAppWidget(context, appWidgetManager, appWidgetIds)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    companion object {

        internal fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager,
                                     appWidgetId: IntArray) {

            val views = RemoteViews(context.packageName, R.layout.currency_widget)
            views.setTextViewText(R.id.btn1, "Refreshing")
            views.setInt(R.id.btn1,"setBackgroundResource",R.color.black_transparent)
            appWidgetManager.updateAppWidget(appWidgetId, views)
            getPrices(context, object : Callback {
                override fun onResponse(call: Call?, response: Response) {
                    if (response.isSuccessful) {
                        var list: ArrayList<Currency> = ArrayList()
                        val res = response.body()!!.string()
                        val array = JSONArray(res)
                        (0 until array.length())
                                .mapTo(list) {
                                    Currency(array.optJSONObject(it))
                                }
                        val tempList: ArrayList<Currency> = ArrayList()
                        list = if (SharedPreference.getInstance(context).hasCustomCurrency()) {
                            val coins: ArrayList<String> = SharedPreference.getInstance(context).getCustomCoins()
                            for (i in 0 until coins.size)
                                tempList.addAll(list.filter { coin -> coin.currencySymbol == coins[i] })
                            tempList
                        } else {
                            ArrayList(list.subList(0, 4))
                        }
                        views.setTextViewText(R.id.btn1, "")
                        views.setInt(R.id.btn1,"setBackgroundResource",android.R.color.transparent)
                        reRenderWidget(context, views, appWidgetManager, appWidgetId, list)
                    }
                }

                override fun onFailure(call: Call?, e: IOException?) {}

            })

        }

        private fun setButtonListener(context: Context, views: RemoteViews, appWidgetId: IntArray) {

            val intent = Intent(context, CurrencyWidget::class.java)
            intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetId)
            intent.putExtra("widgetName","omid")
            val pendingIntent = PendingIntent.getBroadcast(context,
                    3, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            views.setOnClickPendingIntent(R.id.btn1, pendingIntent)

        }

        private fun reRenderWidget(context: Context, views: RemoteViews, appWidgetManager: AppWidgetManager, appWidgetId: IntArray, list: ArrayList<Currency>) {


            setButtonListener(context, views, appWidgetId)
            val currencyFormatter = DecimalFormat("#.##")
            for (i in 0 until list.size) {
                views.setTextViewText(getCurrencyNameViewId(i), list[i].currencySymbol)
                views.setTextViewText(getCurrencySellPriceViewId(i), ": ".plus(currencyFormatter.format(list[i].currencyPriceSell).plus("₺")))
//                views.setTextViewText(getCurrencyBuyPriceViewId(i), context.getString(R.string.buy).plus(currencyFormatter.format(list[i].currencyPriceBuy).plus("₺")))

                if (list[i].currencyPercent!! >= 0.0) {
                    views.setTextViewText(getCurrencyPercentViewId(i), "+".plus(currencyFormatter.format(list[i].currencyPercent)))
                    views.setTextColor(getCurrencyPercentViewId(i), ContextCompat.getColor(context, android.R.color.holo_green_light))
                } else {
                    views.setTextViewText(getCurrencyPercentViewId(i), currencyFormatter.format(list[i].currencyPercent))
                    views.setTextColor(getCurrencyPercentViewId(i), ContextCompat.getColor(context, R.color.red))
                }
            }

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        private fun getPrices(context: Context, callback: Callback) {
            API.instance.run(context.getString(R.string.currency_api), callback)
        }

        private fun getCurrencyNameViewId(index: Int): Int {
            val ids: IntArray = intArrayOf(
                    R.id.appwidget_text_1,
                    R.id.appwidget_text_2,
                    R.id.appwidget_text_3,
                    R.id.appwidget_text_4
            )

            return ids[index]
        }

        private fun getCurrencyBuyPriceViewId(index: Int): Int {
            val ids: IntArray = intArrayOf(
                    R.id.appwidget_buy_1,
                    R.id.appwidget_buy_2,
                    R.id.appwidget_buy_3,
                    R.id.appwidget_buy_4
            )

            return ids[index]
        }
        private fun getCurrencySellPriceViewId(index: Int): Int {
            val ids: IntArray = intArrayOf(
                    R.id.appwidget_sell_1,
                    R.id.appwidget_sell_2,
                    R.id.appwidget_sell_3,
                    R.id.appwidget_sell_4
            )

            return ids[index]
        }

        private fun getCurrencyPercentViewId(index: Int): Int {
            val ids: IntArray = intArrayOf(
                    R.id.appwidget_percent_1,
                    R.id.appwidget_percent_2,
                    R.id.appwidget_percent_3,
                    R.id.appwidget_percent_4
            )

            return ids[index]
        }
    }
}

