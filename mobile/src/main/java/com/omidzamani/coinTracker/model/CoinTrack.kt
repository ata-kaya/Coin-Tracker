package com.omidzamani.coinTracker.model

import java.io.Serializable

@Suppress("unused", "PropertyName")
class CoinTrack

    /*
    {
        "Response": "Success",
        "Type": 100,
        "Aggregated": false,
        "TimeTo": 1534550400,
        "TimeFrom": 1534291200,
        "FirstValueInArray": true,
        "ConversionType": {
            "type": "direct",
            "conversionSymbol": ""
        },
        "Data": [
            {
                "time": 1534291200,
                "close": 6274.22,
                "high": 6620.07,
                "low": 6193.63,
                "open": 6199.63,
                "volumefrom": 132926.33,
                "volumeto": 852103141.83
            }
        ]
    }
     */
    : Serializable {
    var Response: String ? = null
    var TimeFrom: Long ? = null
    var TimeTo: Long ? = null
    var Data: ArrayList<TrackerData> ? = null


}