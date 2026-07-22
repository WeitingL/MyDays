package com.weiting.mydays

import android.app.Application
import com.weiting.mydays.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MyDaysApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MyDaysApp)
            modules(appModule)
        }
    }
}
