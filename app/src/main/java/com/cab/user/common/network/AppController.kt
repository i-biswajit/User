package com.cab.user.common.network

/**
 * @package com.cloneappsolutions.cabmeuser
 * @subpackage network
 * @category AppController
 * @author SMR IT Solutions
 * 
 */

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.LocaleList

import androidx.multidex.MultiDex

import com.google.firebase.FirebaseApp
import com.cab.user.R
import com.cab.user.common.dependencies.component.AppComponent
import com.cab.user.common.dependencies.component.DaggerAppComponent
import com.cab.user.common.dependencies.module.ApplicationModule
import com.cab.user.common.dependencies.module.NetworkModule
import java.util.*

/* ************************************************************
Retrofit Appcomponent and Bufferknife Added
*************************************************************** */
class AppController : Application() {
    private var locale: Locale? = null
    override fun onCreate() {
        super.onCreate()
        setLocale()
        FirebaseApp.initializeApp(this)  // Fire base initialize
        MultiDex.install(this)    // Multiple dex initialize
        instance = this
        appComponent = DaggerAppComponent.builder().applicationModule(ApplicationModule(this)) // This also corresponds to the name of your module: %component_name%Module
                .networkModule(NetworkModule(resources.getString(R.string.base_url))).build()
    }

    private fun setLocale() {
        locale = Locale("en")
        Locale.setDefault(locale)
        val configuration= baseContext.resources.configuration
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            configuration.setLocale(locale)
            val localeList = LocaleList(locale)
            LocaleList.setDefault(localeList)
            configuration.setLocales(localeList)
        } else
            configuration.setLocale(locale)
        baseContext.createConfigurationContext(configuration)
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    companion object {

        val TAG = AppController::class.java.simpleName
        @get:Synchronized
        lateinit var appComponent: AppComponent

        var instance: AppController? = null
            private set


        fun getContext():Context{
            return instance!!.applicationContext
        }

    }

    fun appComponent():AppComponent{
        return appComponent
    }
}