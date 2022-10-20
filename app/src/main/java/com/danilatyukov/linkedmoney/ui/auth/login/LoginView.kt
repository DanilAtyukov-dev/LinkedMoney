package com.danilatyukov.linkedmoney.data.auth

import com.danilatyukov.linkedmoney.BaseView

interface AuthView {
    fun showAuthError()
}

interface LoginView: BaseView, AuthView {
    fun showProgress()
    fun hideProgress()
    fun setEmailError()
    fun setPasswordError()
    fun navigateToSignUp()
    fun navigateToHome()

}