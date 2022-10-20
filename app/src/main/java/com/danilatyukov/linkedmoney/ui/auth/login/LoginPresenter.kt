package com.danilatyukov.linkedmoney.ui.login

import com.danilatyukov.linkedmoney.data.auth.LoginView
import com.danilatyukov.linkedmoney.data.local.preferences.AppPreferences
import com.danilatyukov.linkedmoney.ui.auth.AuthInteractor

interface LoginPresenter {
    fun executeLogin(username: String, password: String)
}

class LoginPresenterImpl(private val view: LoginView) : LoginPresenter,
    AuthInteractor.onAuthFinishedListener,
    LoginInteractor.OnDetailsRetrievalFinishedListener {
    private val interactor: LoginInteractor = LoginInteractorImpl()
    private val preferences: AppPreferences = AppPreferences.create(view.context())

    override fun onPasswordError() {
        view.hideProgress()
        view.setPasswordError()
    }

    override fun onUsernameError() {
        view.hideProgress()
        view.setEmailError()
    }

    override fun onAuthSuccess() {
        interactor.persistAccessToken(preferences)
        interactor.retrieveDetails(preferences, this)
    }

    override fun onAuthError() {
        view.showAuthError()
        view.hideProgress()
    }

    override fun onDetailsRetrievalSuccess() {
        interactor.persistUserDetails(preferences)
        view.hideProgress()
        view.navigateToHome()
    }

    override fun onDetailsRetrievalError() {
        interactor.retrieveDetails(preferences, this)
    }

    override fun executeLogin(username: String, password: String) {
        view.showProgress()
        interactor.login(username, password, this)
    }
}