package com.danilatyukov.linkedmoney.ui.dashboard.pedometer

import android.content.Intent
import android.content.SharedPreferences
import android.view.Gravity
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.danilatyukov.linkedmoney.App
import com.danilatyukov.linkedmoney.appComponent
import com.danilatyukov.linkedmoney.appContext
import com.danilatyukov.linkedmoney.data.local.preferences.RetrievedPreference
import com.danilatyukov.linkedmoney.data.local.preferences.SavedPreference
import com.danilatyukov.linkedmoney.data.remote.FDatabaseWriter
import com.danilatyukov.linkedmoney.model.ForegroundService
import com.google.android.gms.tasks.OnCompleteListener
import java.text.DecimalFormat

class PedometerViewModel : ViewModel(), SharedPreferences.OnSharedPreferenceChangeListener {

    private val _counter = MutableLiveData<String>().apply {
        value = RetrievedPreference.getCurrentSteps().toString()
    }
    val counter: LiveData<String> = _counter


    private val _distance = MutableLiveData<String>().apply {
        value = App.roundFloat(RetrievedPreference.getDistanceGPS() / 1000, "#.#").toString()

    }
    val distance: LiveData<String> = _distance


    // TODO: Implement the ViewModel
    private var numSteps: Int = 0
    private var distanceCounter: Float = 0f

    private lateinit var listener: SharedPreferences.OnSharedPreferenceChangeListener

    init {
        App.it().appComponent.sp.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(p0: SharedPreferences?, p1: String?) {
        numSteps = p0!!.getInt("steps", 0)
        _counter.value = numSteps.toString()

        distanceCounter = p0!!.getFloat("distanceGPS", 0f)
        _distance.value = App.roundFloat(distanceCounter/1000, "#.#").toString()
    }

    fun stepsSaved() {
       val task = FDatabaseWriter.writeSteps(numSteps.toLong()-RetrievedPreference.getSavedSteps())
        task.addOnCompleteListener(OnCompleteListener {
            if (it.isSuccessful) {
                SavedPreference.clearSavedSteps()

                App.it().appComponent.editor.putInt("steps", 0).apply()
                App.it().appComponent.editor.putFloat("distanceGPS", 0f).apply()

                numSteps = 0
                _counter.value = numSteps.toString()

                val toast = Toast.makeText(App.it().appContext, "SUCCESSFUL", Toast.LENGTH_SHORT)

                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()


                val serviceIntent = Intent(App.it().appContext, ForegroundService::class.java)
                App.it().appContext.stopService(serviceIntent)

            } else println("не сохранены")

        })
    }



    override fun onCleared() {
        super.onCleared()
        //unregisterListener()
    }

    fun unregisterListener() {
        App.it().appComponent.sp.unregisterOnSharedPreferenceChangeListener(listener)
    }
}