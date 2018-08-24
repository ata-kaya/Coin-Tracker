package com.omidzamani.coinTracker.model

@Suppress("unused")
class TrackerData {
    /*
    "time": 1534291200,
                "close": 6274.22,
                "high": 6620.07,
                "low": 6193.63,
                "open": 6199.63,
                "volumefrom": 132926.33,
                "volumeto": 852103141.83
     */

    var time: Long? = null
    var close: Double ? = null
    var high: Double ? = null
    var low: Double ? = null
    var open: Double ? = null
    var volumefrom: Double ? = null
    var volumeto: Double ? = null
}