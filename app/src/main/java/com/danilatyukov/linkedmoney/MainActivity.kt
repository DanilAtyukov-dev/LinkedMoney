package com.danilatyukov.linkedmoney

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.danilatyukov.linkedmoney.data.local.preferences.RetrievedPreference
import com.danilatyukov.linkedmoney.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : BaseActivity() {

    private lateinit var amb: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        App.it().mainActivity = this

        amb = ActivityMainBinding.inflate(layoutInflater)
        setContentView(amb.root)

        val navView: BottomNavigationView = amb.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        geolocationRequest()
        navView.setupWithNavController(navController)
        showToast("${RetrievedPreference.getEmail()}, ${RetrievedPreference.getUsername()}")

        appComponent.fDatabaseReader
    }

    fun lockInterface(){
        val ll = findViewById<LinearLayout>(R.id.pleaseUpdate)
        ll.visibility = View.VISIBLE

        val nav = findViewById<BottomNavigationView>(R.id.nav_view)
        nav.visibility = View.GONE

        val fr = findViewById<FragmentContainerView>(R.id.nav_host_fragment_activity_main)
        fr.visibility = View.GONE

        val updateButton = findViewById<TextView>(R.id.updateTv)
        updateButton.setOnClickListener{
            val i = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.danilatyukov.linkedmoney"))
            startActivity(i)
        }
    }

}