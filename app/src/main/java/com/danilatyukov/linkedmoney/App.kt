package com.danilatyukov.linkedmoney

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.Settings
import com.danilatyukov.linkedmoney.data.local.preferences.RetrievedPreference
import com.danilatyukov.linkedmoney.data.local.preferences.SavedPreference
import com.danilatyukov.linkedmoney.data.remote.FDatabaseWriter
import java.math.BigDecimal
import java.text.DecimalFormat


class App : Application() {
    lateinit var appComponent: AppComponent
    lateinit var context: Context
    public var mainActivity: MainActivity? = null

    companion object {
        val version: Long = 6
        lateinit var ANDROID_ID: String

        lateinit var app: App
        fun it(): App {
            return app
        }

        fun roundFloat(f: Float, pattern: String): String{
            val df = DecimalFormat(pattern)
            return df.format(f)
        }

        fun getAllStepsPrice(): String{
            val b = BigDecimal(RetrievedPreference.getStepPrice())

            var itemCost  = BigDecimal.ZERO;
            var totalCost = BigDecimal.ZERO;


                itemCost  = b.multiply(BigDecimal.valueOf(RetrievedPreference.getConfirmSteps().toDouble()));
                totalCost = totalCost.add(itemCost);
                return totalCost.toString();


            return b.multiply(BigDecimal(RetrievedPreference.getAllSteps())).toString()
        }

        fun getStepPrice(doksp: Int, dkpp: Int, krv: Int, sopr: Float): String {


            val ka = RetrievedPreference.getka()
            val kr = 5

            val odpp = sopr*dkpp
            val ddka = odpp*ka/100


            val odppvka = odpp - ddka

            //бонус реферерру
            val ddkr = odppvka*kr/100
            //предыдущий записанный бонус

            val wrb = RetrievedPreference.getWrittenRefBonus()
            if (wrb<ddkr.toDouble()) {
                FDatabaseWriter.writtenRefBonus(ddkr.toDouble())
                FDatabaseWriter.writeReferrerBonus(ddkr.toDouble() - wrb)
            }


            val idpp = odppvka-ddkr


            val rssr = idpp/doksp
            val ssv = krv*rssr



            val stepPrice = if (ssv.toString().equals("NaN") || ssv.toString().equals("Infinity")) "0.0" else ssv.toBigDecimal().toString()
            SavedPreference.setStepPrice(stepPrice)


            return stepPrice
        }
    }

    @SuppressLint("HardwareIds")
    override fun onCreate() {
        super.onCreate()

        app = this
        appComponent = DaggerAppComponent.create()
        context = this

        appComponent.sp.registerOnSharedPreferenceChangeListener(listener)

        ANDROID_ID = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID);

    }

    fun vibratePhone() {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= 26) {
            vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(100)
        }
    }


    private val listener =
        OnSharedPreferenceChangeListener { sharedPreferences, key ->

        }

    fun unregisterListener() {
        appComponent.sp.unregisterOnSharedPreferenceChangeListener(listener)
    }
}



val Context.appComponent: AppComponent
    get() = when (this) {
        is App -> appComponent
        else -> this.applicationContext.appComponent
    }
val Context.appContext: Context
    get() = when (this) {
        is App -> context
        else -> this.applicationContext.appContext
    }

/*
var Context.mainActivity: MainActivity
    get() = when (this) {
        is App -> mainActivity
        else -> this.applicationContext.mainActivity
    }*/
