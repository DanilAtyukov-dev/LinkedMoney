package com.danilatyukov.linkedmoney.ui.home.referralData

import android.content.ClipData
import android.content.ClipboardManager

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.danilatyukov.linkedmoney.App
import com.danilatyukov.linkedmoney.R
import com.danilatyukov.linkedmoney.databinding.FragmentReferralDataBinding
import com.danilatyukov.linkedmoney.ui.InfoDialogFragment


class ReferralDataFragment : Fragment() {

    private lateinit var _binding: FragmentReferralDataBinding
    private val binding get() = _binding

    lateinit var referralLink: String

    companion object {
        fun newInstance() = ReferralDataFragment()
    }

    private lateinit var viewModel: ReferralDataViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentReferralDataBinding.inflate(inflater, container, false)
        val root = binding.root

        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ReferralDataViewModel::class.java)
        // TODO: Use the ViewModel

        viewModel.referralLink.observe(viewLifecycleOwner){
            referralLink = it
        }

        viewModel.referralNum.observe(viewLifecycleOwner){
            _binding.allReferalsTv.text = it
        }

        viewModel.referralBonus.observe(viewLifecycleOwner){
            _binding.allReferalBonusTv.text = it
        }


        _binding.referralLinkTv.setOnClickListener {
            App.it().vibratePhone()
            val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("referralLink", referralLink)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(requireContext(), "скопировано", Toast.LENGTH_SHORT).show()
        }

        _binding.refLinkInfoImg.setOnClickListener {
            App.it().vibratePhone()
            InfoDialogFragment(
                "Реферальная программа",
                "Вы получаете бонус в размере 5% от заработанных вашими рефералами реальных денег. К тому же реферальные средства не подвержены волатильности и могут только расти.",
                R.drawable.reficon
            ).show(parentFragmentManager.beginTransaction(), "info")
        }


    }

}