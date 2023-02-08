package com.sportbuff.demo

import android.app.Application
import com.buffup.sdk.BuffSdk

class SpotBuffApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        BuffSdk.initialize(
            context = this,
            clientAccount = "sportbuff"
        )
    }
}