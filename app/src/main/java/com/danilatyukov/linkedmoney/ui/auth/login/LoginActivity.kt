package com.danilatyukov.linkedmoney.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.danilatyukov.linkedmoney.BaseActivity
import com.danilatyukov.linkedmoney.MainActivity
import com.danilatyukov.linkedmoney.R
import com.danilatyukov.linkedmoney.data.auth.LoginView
import com.danilatyukov.linkedmoney.data.local.preferences.AppPreferences
import com.danilatyukov.linkedmoney.databinding.ActivityAuthBinding
import com.danilatyukov.linkedmoney.ui.login.LoginPresenter
import com.danilatyukov.linkedmoney.ui.login.LoginPresenterImpl
import kotlinx.android.synthetic.main.activity_auth.*

class AuthActivity : BaseActivity(), LoginView, View.OnClickListener {

    lateinit var presenter: LoginPresenter
    lateinit var preferences: AppPreferences

    private lateinit var _binding: ActivityAuthBinding
    private val binding get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(_binding.root)
        presenter = LoginPresenterImpl(this)
        preferences = AppPreferences.create(this)
        bindViews()

    }

    private fun bindViews(){
        _binding.btnLogin.setOnClickListener(this)
        _binding.btnLogin.setOnClickListener(this)
    }

    override fun showAuthError() {
        showToast("Неверный email или пароль.")
    }

    override fun onClick(view: View) {
        if (view.id == R.id.btnLogin){
            presenter.executeLogin(etEmail.text.toString(), etPassword.text.toString())
        } else if (view.id == R.id.btnSignUp) {
            navigateToSignUp()
        }
    }

    override fun hideProgress() {
        _binding.progressBar.visibility = View.GONE
    }

    override fun showProgress() {
        _binding.progressBar.visibility = View.VISIBLE
    }

    override fun setEmailError() {
        etEmail.error = "Поле не должно быть пустым"
    }

    override fun setPasswordError() {
        etPassword.error = "Поле не может быть пустым"
    }

    override fun navigateToSignUp() {
        TODO("Not yet implemented")
    }

    override fun navigateToHome() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }




}