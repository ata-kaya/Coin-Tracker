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
import com.omidzamani.coinTracker.model.Currency
import com.omidzamani.coinTracker.interfaces.CurrencyResponse
import com.omidzamani.coinTracker.utils.CURRENCY_ALLOWED_SIZE
import com.omidzamani.coinTracker.utils.SharedPreference
import org.jetbrains.anko.longToast

/**
 * Implementation of App Widget functionality.
 */
class CurrencyWidget : AppWidgetProvider() {

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

            val views = RemoteViews(context.packageName, R.layout.currency_widget)
            views.setTextViewText(R.id.btn1, context.getString(R.string.refreshing))
            views.setInt(R.id.btn1, "setBackgroundResource", R.color.black_transparent)
            appWidgetManager.updateAppWidget(appWidgetId, views)
            API.instance.getCurrencies(object: CurrencyResponse {
                override fun onResponse(currencies: ArrayList<Currency>) {
                    val tempList: ArrayList<Currency> = ArrayList()
                    var list = currencies
                    list = if (SharedPreference.getInstance(context).hasCustomCurrency()) {
                        val subCurrencies: ArrayList<String> = SharedPreference.getInstance(context).getCustomCurrencies()
                        for (i in 0 until subCurrencies.size)
                            tempList.addAll(list.filter { currency -> currency.currencySymbol == subCurrencies[i] })
                        tempList
                    } else {
                        ArrayList(list.subList(0, CURRENCY_ALLOWED_SIZE))
                    }
                    views.setTextViewText(R.id.btn1, "")
                    views.setInt(R.id.btn1, "setBackgroundResource", android.R.color.transparent)
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

            val intent = Intent(context, CurrencyWidget::class.java)
            intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetId)
            val pendingIntent = PendingIntent.getBroadcast(context,
                    3, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            views.setOnClickPendingIntent(R.id.btn1, pendingIntent)

        }

        private fun reRenderWidget(context: Context, views: RemoteViews, appWidgetManager: AppWidgetManager, appWidgetId: IntArray, list: ArrayList<Currency>) {


            setButtonListener(context, views, appWidgetId)
            for (i in 0 until list.size) {
                views.setTextViewText(getCurrencyNameViewId(i), list[i].currencySymbol)
                views.setTextViewText(getCurrencySellPriceViewId(i), ": ".plus(String.format("%.2f", list[i].currencyPriceSell).plus("₺")))
//                views.setTextViewText(getCurrencyBuyPriceViewId(i), context.getString(R.string.buy).plus(currencyFormatter.format(list[i].currencyPriceBuy).plus("₺")))

                if (list[i].currencyPercent!! >= 0.0) {
                    views.setTextViewText(getCurrencyPercentViewId(i), "+".plus(String.format("%.2f", list[i].currencyPercent)).plus("%"))
                    views.setTextColor(getCurrencyPercentViewId(i), ContextCompat.getColor(context, android.R.color.holo_green_light))
                } else {
                    views.setTextViewText(getCurrencyPercentViewId(i), String.format("%.2f", list[i].currencyPercent).plus("%"))
                    views.setTextColor(getCurrencyPercentViewId(i), ContextCompat.getColor(context, R.color.red))
                }
            }

            appWidgetManager.updateAppWidget(appWidgetId, views)
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

