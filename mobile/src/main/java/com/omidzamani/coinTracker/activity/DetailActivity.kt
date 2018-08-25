package com.omidzamani.coinTracker.activity

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.content.ContextCompat
import android.support.v7.widget.AppCompatTextView
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.omidzamani.coinTracker.API
import com.omidzamani.coinTracker.R
import com.omidzamani.coinTracker.interfaces.CoinTrackerResponse
import com.omidzamani.coinTracker.model.Coin
import com.omidzamani.coinTracker.model.CoinTrack
import com.omidzamani.coinTracker.model.Currency
import com.omidzamani.coinTracker.model.TrackerData
import com.omidzamani.coinTracker.utils.TrackerType
import kotlinx.android.synthetic.main.activity_detail.*
import org.jetbrains.anko.longToast
import java.io.Serializable
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class DetailActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {


    private var isCoin: Boolean = false
    private var mainData: Serializable ? = null
    private val chartDateFormat = SimpleDateFormat("MMM dd yyyy HH:mm", Locale.getDefault())
    private var whiteColor: Int = 0
    private val currencyFormatter =  NumberFormat.getCurrencyInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        whiteColor = ContextCompat.getColor(this, R.color.item_background_color)
        currencyFormatter.currency = java.util.Currency.getInstance("USD")

        mainData = when {
            intent.getSerializableExtra("Value") is Coin -> {
                isCoin = true
                intent.getSerializableExtra("Value") as Coin
            }
            intent.getSerializableExtra("Value") is Currency -> {
                isCoin = false
                intent.getSerializableExtra("Value") as Currency
            }
            else -> null

        }
        selected_price.text =  currencyFormatter.format((mainData as Coin).coinPrice)
        date.text =  chartDateFormat.format(Date())
        createChart()
        val type = TrackerType.DAILY
        getTrackingData(type)

        val adapter = ArrayAdapter(this,android.R.layout.simple_spinner_item, resources.getStringArray(R.array.tracking_mode))
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = this
    }

    private fun getTrackingData(type: TrackerType) {
        API.instance.getCoinDetail(type.method(), (mainData as Coin).coinSymbol!!, "USD", type.limit(), object : CoinTrackerResponse {
            override fun onResponse(coinTrack: CoinTrack) {
                val data: ArrayList<TrackerData> = coinTrack.Data!!
                setPrices()
                setChart(data)
            }

            override fun onFail(responseCode: Int) {
                Handler(Looper.getMainLooper()).post {
                    longToast(R.string.some_wrong)
                }
            }

        })
    }

    private fun setPrices() {
        val mainCoinData = (mainData as Coin)
        val red = ContextCompat.getColor(this, R.color.red)
        val green = ContextCompat.getColor(this, android.R.color.holo_green_light)
        Handler(Looper.getMainLooper()).post {
            coin_name.text =  mainCoinData.coinSymbol.plus(", ").plus(mainCoinData.coinName)
            price_usd_content.text = currencyFormatter.format(mainCoinData.coinPrice)
            price_btc_content.text = mainCoinData.coinPriceBTC
            an_hour_change_content.setTextColor(red)
            a_day_change_content.setTextColor(red)
            a_week_change_content.setTextColor(red)
            if (java.lang.Float.parseFloat(mainCoinData.coinPercent.toString()) >= 0.0) {
                an_hour_change_content.setTextColor(green)
            }
            if (java.lang.Float.parseFloat(mainCoinData.coinPercent24h.toString()) >= 0.0) {
                a_day_change_content.setTextColor(green)
            }
            if (java.lang.Float.parseFloat(mainCoinData.coinPercent7d.toString()) >= 0.0) {
                a_week_change_content.setTextColor(green)
            }
            an_hour_change_content.text = mainCoinData.coinPercent.plus("%")
            a_day_change_content.text = mainCoinData.coinPercent24h.plus("%")
            a_week_change_content.text = mainCoinData.coinPercent7d.plus("%")
        }
    }

    private fun createChart() {
        chart.setBackgroundColor(Color.BLACK)
        chart.setMaxVisibleValueCount(30)
        chart.axisRight.isEnabled = false
        chart.description.isEnabled = false
        chart.legend.isEnabled = false
        chart.isAutoScaleMinMaxEnabled = true
        chart.animateX(2000, Easing.EasingOption.EaseInOutExpo)
        chart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onNothingSelected() {

            }

            override fun onValueSelected(e: Entry?, h: Highlight) {
                Handler(Looper.getMainLooper()).post {
                    val dateString = chartDateFormat.format(h.x.toLong() * 1000)
                    selected_price.text = currencyFormatter.format(h.y.toDouble())
                    date.text = dateString
                }
            }

        })


        val leftYAxis = chart.axisLeft
        leftYAxis.valueFormatter = IAxisValueFormatter { value, _ ->
            currencyFormatter.format(value.toDouble())
        }
        leftYAxis.textColor = whiteColor
        leftYAxis.gridColor = whiteColor
        leftYAxis.spaceTop = 25f
        leftYAxis.setLabelCount(4, false)


        chart.xAxis.textColor = whiteColor
        chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        chart.xAxis.setLabelCount(5, false)
        chart.xAxis.valueFormatter = IAxisValueFormatter { value, _ ->
            SimpleDateFormat("dd MMM yy", Locale.getDefault()).format(Date(value.toLong() * 1000))
        }
    }

    private fun setChart(data: ArrayList<TrackerData>) {
        val entry: ArrayList<Entry> = ArrayList()
        (0 until data.size).mapTo(entry) {
            Entry(data[it].time!!.toFloat(),data[it].close!!.toFloat())
        }

        val dataSet = LineDataSet(entry,"Label")
        dataSet.setDrawCircles(false)
        dataSet.color = whiteColor
        dataSet.valueTextColor = whiteColor
        dataSet.fillAlpha = 0
        dataSet.lineWidth = 1f
        dataSet.axisDependency = YAxis.AxisDependency.LEFT


        chart.data = LineData(dataSet)
        chart.data.isHighlightEnabled = true
        Handler(Looper.getMainLooper()).post {
            chart.invalidate()
        }

    }

    //spinner impl
    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val selectedText = (view as AppCompatTextView).text.toString()
        val type = when {
            selectedText.toLowerCase() == TrackerType.WEEKLY.toString().toLowerCase() -> TrackerType.WEEKLY
            selectedText.toLowerCase() == TrackerType.MONTHLY.toString().toLowerCase() -> TrackerType.MONTHLY
            else -> TrackerType.DAILY
        }
        getTrackingData(type)
    }



}
