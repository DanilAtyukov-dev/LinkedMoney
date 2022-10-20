package com.danilatyukov.linkedmoney.model.pedometer

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.danilatyukov.linkedmoney.App
import com.danilatyukov.linkedmoney.appComponent
import com.danilatyukov.linkedmoney.appContext
import com.danilatyukov.linkedmoney.data.local.preferences.SavedPreference
import com.danilatyukov.linkedmoney.data.remote.FDatabaseWriter
import com.danilatyukov.linkedmoney.model.pedometer.listener.StepListener
import com.danilatyukov.linkedmoney.model.pedometer.utils.StepDetector

class PedometerManager: SensorEventListener, StepListener {

    var isActive = false

    // TODO: Implement the ViewModel
    private var simpleStepDetector: StepDetector? = null
    private var sensorManager: SensorManager? = null
    private var numSteps: Int = 0



    init {
        SavedPreference.clearSavedSteps()
        // Get an instance of the SensorManager
        sensorManager = App.it().appContext.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        simpleStepDetector = StepDetector()
        simpleStepDetector!!.registerListener(this)
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event!!.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            simpleStepDetector!!.updateAccelerometer(
                event.timestamp,
                event.values[0],
                event.values[1],
                event.values[2]
            )
        }
    }

    override fun step(timeNs: Long) {

            SavedPreference.incrementAllSteps(1)
        numSteps++

        if (numSteps%100==0){
            SavedPreference.incrementSavedSteps(100)
            FDatabaseWriter.writeSteps(100)
        }




        App.it().appComponent.editor.putInt("steps", numSteps)
        App.it().appComponent.editor.commit()
    }

    fun start(){
        numSteps = 0
        sensorManager!!.registerListener(
            this,
            sensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_FASTEST
        )
    }

    fun stop(){
        sensorManager!!.unregisterListener(this)
    }

}