package com.danilatyukov.linkedmoney.ui.home.referralData

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.danilatyukov.linkedmoney.App
import com.danilatyukov.linkedmoney.appComponent
import com.danilatyukov.linkedmoney.data.local.preferences.RetrievedPreference
import com.danilatyukov.linkedmoney.model.ReferralProgram

class ReferralDataViewModel : ViewModel(), SharedPreferences.OnSharedPreferenceChangeListener {
    // TODO: Implement the ViewModel
    init {
        App.it().appComponent.sp.registerOnSharedPreferenceChangeListener(this)
    }

    private val _referralLink = MutableLiveData<String>().apply {
        value = ReferralProgram.referralLink
    }
    var referralLink: LiveData<String> = _referralLink

    private val _referralNum = MutableLiveData<String>().apply {
        value = RetrievedPreference.getRefNum().toString()
    }
    var referralNum: LiveData<String> = _referralNum

    private val _referralBonus = MutableLiveData<String>().apply {
        value = App.roundFloat(RetrievedPreference.getRefBonus(), "#.#####")
    }
    var referralBonus: LiveData<String> = _referralBonus

    override fun onSharedPreferenceChanged(p0: SharedPreferences?, p1: String?) {
        _referralNum.value = p0!!.getInt("referrals", 0).toString()
        _referralBonus.value = App.roundFloat(p0.getFloat("refBonus", 0f), "#.#####")

    }
}