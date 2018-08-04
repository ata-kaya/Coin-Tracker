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
import com.omidzamani.creptocoin.BuildConfig
import com.omidzamani.creptocoin.R
import com.omidzamani.creptocoin.R.id.btn
import com.omidzamani.creptocoin.model.Coin
import com.omidzamani.creptocoin.utils.SharedPreference
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONArray
import java.io.IOException

/**
 * Implementation of App Widget functionality.
 */
class CoinWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
        Log.d("OMID", "coin ids="+appWidgetId)
            updateAppWidget(context, appWidgetManager, appWidgetIds)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent!!.hasExtra("widgetName") && intent.getStringExtra("widgetName").equals("omid")) {
            val dd =intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS)
            Log.d("omid","this is onrecieve="+dd[0])
            if (dd.size > 1)
                Log.d("omid","this is onrecieve="+ dd[1])
        } else if (intent!!.hasExtra("widgetName") && intent.getStringExtra("widgetName").equals("omid2")) {
            val dd = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS)
            Log.d("omid","this is onrecieve2="+dd[0])
            if (dd.size > 1)
                Log.d("omid","this is onrecieve2="+dd[1])
        } else
            super.onReceive(context, intent)
    }


    companion object {

        internal fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager,
                                     appWidgetId: IntArray) {

            val views = RemoteViews(context.packageName, R.layout.coin_widget)
            views.setTextViewText(R.id.btn, "Refreshing")
            appWidgetManager.updateAppWidget(appWidgetId, views)
            getPrices(context, object : Callback {
                override fun onResponse(call: Call?, response: Response) {
                    if (response.isSuccessful) {
                        var list: ArrayList<Coin> = ArrayList()
                        val res = response.body()!!.string()
                        val array = JSONArray(res)
                        (0 until array.length())
                                .mapTo(list) {
                                    Coin(array.optJSONObject(it))
                                }
                        val tempList: ArrayList<Coin> = ArrayList()
                        list = if (SharedPreference.getInstance(context).hasCustomCoin()) {
                            val coins: ArrayList<String> = SharedPreference.getInstance(context).getCustomCoins()
                            for (i in 0 until coins.size)
                                tempList.addAll(list.filter { coin -> coin.coinSymbol == coins[i] })
                            tempList
                        } else {
                            ArrayList(list.subList(0, 6))
                        }
                        views.setTextViewText(btn, "")
                        reRenderWidget(context, views, appWidgetManager, appWidgetId, list)
                    }
                }

                override fun onFailure(call: Call?, e: IOException?) {}

            })

        }


        private fun setButtonListener(context: Context, views: RemoteViews, appWidgetId: IntArray) {

            val intent = Intent(context, CoinWidget::class.java)
            intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetId)
            intent.putExtra("widgetName","omid2")
            val pendingIntent = PendingIntent.getBroadcast(context,
                    0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            views.setOnClickPendingIntent(R.id.btn, pendingIntent)

        }

        private fun reRenderWidget(context: Context, views: RemoteViews, appWidgetManager: AppWidgetManager, appWidgetId: IntArray, list: ArrayList<Coin>) {


            setButtonListener(context, views, appWidgetId)
            for (i in 0 until list.size) {
                views.setTextViewText(getCoinViewId(i), list[i].coinSymbol.plus(", ").plus(list[i].coinName))
                views.setTextViewText(getCoinPriceViewId(i), "$".plus(list[i].coinPrice))

                if (java.lang.Float.parseFloat(list[i].coinPercent.toString()) >= 0.0) {
                    views.setTextViewText(getCoinPercentViewId(i), "+".plus(list[i].coinPercent))
                    views.setTextColor(getCoinPercentViewId(i), ContextCompat.getColor(context, R.color.green))
                } else {
                    views.setTextViewText(getCoinPercentViewId(i), list[i].coinPercent)
                    views.setTextColor(getCoinPercentViewId(i), ContextCompat.getColor(context, R.color.red))
                }
            }

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        private fun getPrices(context: Context, callback: Callback) {

            API.instance.run(context.getString(R.string.coin_api), callback)
        }

        private fun getCoinViewId(index: Int): Int {
            val ids: IntArray = intArrayOf(
                    R.id.appwidget_text1,
                    R.id.appwidget_text2,
                    R.id.appwidget_text3,
                    R.id.appwidget_text4,
                    R.id.appwidget_text5,
                    R.id.appwidget_text6
            )

            return ids[index]
        }

        private fun getCoinPriceViewId(index: Int): Int {
            val ids: IntArray = intArrayOf(
                    R.id.appwidget_price1,
                    R.id.appwidget_price2,
                    R.id.appwidget_price3,
                    R.id.appwidget_price4,
                    R.id.appwidget_price5,
                    R.id.appwidget_price6
            )

            return ids[index]
        }

        private fun getCoinPercentViewId(index: Int): Int {
            val ids: IntArray = intArrayOf(
                    R.id.appwidget_text_1,
                    R.id.appwidget_text_2,
                    R.id.appwidget_text_3,
                    R.id.appwidget_text_4,
                    R.id.appwidget_text_5,
                    R.id.appwidget_text_6
            )

            return ids[index]
        }
    }
}
