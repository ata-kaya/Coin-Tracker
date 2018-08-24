package com.omidzamani.coinTracker.utils

import com.omidzamani.coinTracker.interfaces.TrackerTypesInterface

enum class TrackerType : TrackerTypesInterface {


    DAILY {
        override fun method(): String {
           return "histominute"
        }

        override fun limit() : String {
            return "1440"
        }
    },
    WEEKLY {
        override fun method(): String {
            return "histohour"
        }

        override fun limit() : String {
            return "167"
        }
    },
    MONTHLY {
        override fun method(): String {
            return "histohour"
        }

        override fun limit() : String {
            return "730"
        }
    }

}
