package com.danilatyukov.linkedmoney.ui.home.scores

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.danilatyukov.linkedmoney.App
import com.danilatyukov.linkedmoney.appComponent
import com.danilatyukov.linkedmoney.data.local.preferences.RetrievedPreference
import com.danilatyukov.linkedmoney.data.local.preferences.SavedPreference
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.math.BigDecimal

class ScoresViewModel : ViewModel(), SharedPreferences.OnSharedPreferenceChangeListener {
    // TODO: Implement the ViewModel

    init {
        App.it().appComponent.sp.registerOnSharedPreferenceChangeListener(this)
        /*setStepPriceListener()
        setConfirmStepsListener()*/
    }
    private val _confirmSteps = MutableLiveData<String>().apply {
        value = RetrievedPreference.getConfirmSteps().toString()
    }
    val confirmSteps: LiveData<String> = _confirmSteps




    private val _allMoney = MutableLiveData<String>().apply {
        value = App.getAllStepsPrice()
    }
    val allMoney: LiveData<String> = _allMoney

    private val _allSteps = MutableLiveData<Int>().apply {
        value = RetrievedPreference.getAllSteps()
    }
    val allSteps: LiveData<Int> = _allSteps

    private val _allGpsDistance = MutableLiveData<String>().apply {
        //value = "9.3"
        value = App.roundFloat(RetrievedPreference.getAllDistanceGPS()/1000, "#.#")
    }
    val allGpsDistance: LiveData<String> = _allGpsDistance

    private val _stepPrice = MutableLiveData<String>().apply {
        value = App.getStepPrice(/*p0.getInt("confirmSteps", 0)*/ RetrievedPreference.getConfirmSteps(),  RetrievedPreference.getAds(), RetrievedPreference.getKrv(), RetrievedPreference.getSopr())
    }
    val stepPrice: LiveData<String> = _stepPrice

    override fun onSharedPreferenceChanged(p0: SharedPreferences?, p1: String?) {
        if (p0 == null) return
        _allSteps.value = p0.getInt("allSteps", 0)
        _allGpsDistance.value = App.roundFloat(p0.getFloat("AllDistanceGPS", 0f)/1000, "#.#")
        _confirmSteps.value = p0.getInt("confirmSteps", 0).toString()


        if(p1.equals("ads") || p1.equals("confirmSteps") || p1.equals("sopr") || p1.equals("krv")){
        _stepPrice.value = App.getStepPrice(p0.getInt("confirmSteps", 0),  p0.getInt("ads", 0), p0.getInt("krv", 10), p0.getFloat("sopr", 0.01f))

            /*SavedPreference.setStepPrice(_stepPrice.value!!)*/

            _allMoney.value = App.getAllStepsPrice()
        }
    }
}