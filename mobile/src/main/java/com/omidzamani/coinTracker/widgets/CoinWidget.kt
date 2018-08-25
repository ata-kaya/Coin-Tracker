package com.omidzamani.coinTracker.widgets

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.support.v4.content.ContextCompat
import android.widget.RemoteViews
import com.omidzamani.coinTracker.API
import com.omidzamani.coinTracker.R
import com.omidzamani.coinTracker.R.id.btn
import com.omidzamani.coinTracker.model.Coin
import com.omidzamani.coinTracker.interfaces.CoinResponse
import com.omidzamani.coinTracker.utils.SharedPreference
import org.jetbrains.anko.longToast

/**
 * Implementation of App Widget functionality.
 */
class CoinWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
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

            val views = RemoteViews(context.packageName, R.layout.coin_widget)
            views.setTextViewText(R.id.btn, context.getString(R.string.refreshing))
            views.setInt(R.id.btn, "setBackgroundResource", R.color.black_transparent)
            appWidgetManager.updateAppWidget(appWidgetId, views)
            API.instance.getCoins(object: CoinResponse {
                override fun onResponse(coins: ArrayList<Coin>) {
                    val tempList: ArrayList<Coin> = ArrayList()
                    var list = coins
                    list = if (SharedPreference.getInstance(context).hasCustomCoin()) {
                        val subCoins: ArrayList<String> = SharedPreference.getInstance(context).getCustomCoins()
                        for (i in 0 until subCoins.size)
                            tempList.addAll(list.filter { coin -> coin.coinSymbol == subCoins[i] })
                        tempList
                    } else {
                        ArrayList(list.subList(0, 4))
                    }
                    views.setTextViewText(btn, "")
                    views.setInt(R.id.btn, "setBackgroundResource", android.R.color.transparent)
                    reRenderWidget(context, views, appWidgetManager, appWidgetId, list)
                }

                override fun onFail(responseCode: Int) {
                    Handler(Looper.getMainLooper()).post {
                        context.longToast(R.string.some_wrong)
                    }
                }

            })

        }


        private fun setButtonListener(context: Context, views: RemoteViews, appWidgetId: IntArray) {

            val intent = Intent(context, CoinWidget::class.java)
            intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetId)
            val pendingIntent = PendingIntent.getBroadcast(context,
                    0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            views.setOnClickPendingIntent(R.id.btn, pendingIntent)

        }

        private fun reRenderWidget(context: Context, views: RemoteViews, appWidgetManager: AppWidgetManager, appWidgetId: IntArray, list: ArrayList<Coin>) {


            setButtonListener(context, views, appWidgetId)
            for (i in 0 until list.size) {
                views.setTextViewText(getCoinViewId(i), list[i].coinSymbol.plus(", ").plus(list[i].coinName))
                views.setTextViewText(getCoinPriceViewId(i), "$".plus(String.format("%.2f", list[i].coinPrice)))

                if (java.lang.Float.parseFloat(list[i].coinPercent.toString()) >= 0.0) {
                    views.setTextViewText(getCoinPercentViewId(i), "+".plus(list[i].coinPercent).plus("%"))
                    views.setTextColor(getCoinPercentViewId(i), ContextCompat.getColor(context, android.R.color.holo_green_light))
                } else {
                    views.setTextViewText(getCoinPercentViewId(i), list[i].coinPercent.plus("%"))
                    views.setTextColor(getCoinPercentViewId(i), ContextCompat.getColor(context, R.color.red))
                }
            }

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        private fun getCoinViewId(index: Int): Int {
            val ids: IntArray = intArrayOf(
                    R.id.appwidget_text1,
                    R.id.appwidget_text2,
                    R.id.appwidget_text3,
                    R.id.appwidget_text4
            )

            return ids[index]
        }

        private fun getCoinPriceViewId(index: Int): Int {
            val ids: IntArray = intArrayOf(
                    R.id.appwidget_price1,
                    R.id.appwidget_price2,
                    R.id.appwidget_price3,
                    R.id.appwidget_price4
            )

            return ids[index]
        }

        private fun getCoinPercentViewId(index: Int): Int {
            val ids: IntArray = intArrayOf(
                    R.id.appwidget_text_1,
                    R.id.appwidget_text_2,
                    R.id.appwidget_text_3,
                    R.id.appwidget_text_4
            )

            return ids[index]
        }
    }
}

