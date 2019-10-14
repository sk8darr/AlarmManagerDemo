package com.cloudsourceit.alarmmanagerdemo

import android.app.Application
import com.cloudsourceit.alarmmanagerdemo.utils.createNotificationChannel

class App : Application(){

    override fun onCreate() {
        super.onCreate()
        ObjectBox.init(this)
        createNotificationChannel(this)
    }
}