package com.danilatyukov.linkedmoney.ui.dashboard.pedometer


import android.app.ActivityManager
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Chronometer
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.danilatyukov.linkedmoney.App
import com.danilatyukov.linkedmoney.data.local.preferences.RetrievedPreference
import com.danilatyukov.linkedmoney.databinding.FragmentPedometerBinding
import com.danilatyukov.linkedmoney.model.ForegroundService

class PedometerFragment : Fragment() {

    private lateinit var chronometer: Chronometer

    /* companion object {
        fun newInstance() = PedometerFragment()
    }*/

    private lateinit var _binding: FragmentPedometerBinding
    private val binding get() = _binding

    private lateinit var viewModel: PedometerViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPedometerBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val pedometerViewModel = ViewModelProvider(this)[PedometerViewModel::class.java]

        val pedometerCount: TextView = binding.tvPedometer
        pedometerViewModel.counter.observe(viewLifecycleOwner) {
            pedometerCount.text = it
        }

        val distanceCount: TextView = binding.tvOdometer
        pedometerViewModel.distance.observe(viewLifecycleOwner) {
            distanceCount.text = it
        }

        setBtnState()
        _binding.pedometerButton.setOnClickListener {
            App.it().vibratePhone()
            onChangeClick()
        }

        return root
    }

    private fun setBtnState(){
        if (isMyServiceRunning(ForegroundService::class.java)){
            _binding.pedometerButton.text = "Сохранить"
            _binding.pedometerButton.tag = "active"
        } else {
            _binding.pedometerButton.text = "Начать"
            _binding.pedometerButton.tag = "stopped"
        }
    }

    private fun onChangeClick(){
        val btnState = _binding.pedometerButton.tag
        val serviceIntent = Intent(requireContext(), ForegroundService::class.java)

        if (btnState=="active"){
            _binding.pedometerButton.tag = "stopped"
            _binding.pedometerButton.text = "Начать"
            viewModel.stepsSaved()
            //activity?.stopService(serviceIntent)
        }
        else if(btnState=="stopped"){
            _binding.pedometerButton.tag = "active"
            _binding.pedometerButton.text = "Сохранить"
            ContextCompat.startForegroundService(requireContext(), serviceIntent)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this)[PedometerViewModel::class.java]
    }

    private fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = activity?.getSystemService(AppCompatActivity.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }
}