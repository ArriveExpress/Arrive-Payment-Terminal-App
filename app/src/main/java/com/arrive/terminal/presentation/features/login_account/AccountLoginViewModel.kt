package com.arrive.terminal.presentation.features.login_account;

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavOptions
import com.arrive.terminal.R
import com.arrive.terminal.core.ui.base.BaseViewModel
import com.arrive.terminal.core.ui.extensions.valueOrEmpty
import com.arrive.terminal.core.ui.model.StringValue.Companion.asStringValue
import com.arrive.terminal.core.ui.utils.withProgress
import com.arrive.terminal.domain.manager.CustomerManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountLoginViewModel @Inject constructor(
    private val customerManager: CustomerManager
) : BaseViewModel() {

    val accountNumber = MutableLiveData<String>()
    val pin = MutableLiveData<String>()
    val isActionButtonClickable = MutableLiveData(true)

    val driverId = MutableLiveData<String>()
    val loginProgress = MutableLiveData<Boolean>()

    fun onLoginClick() {
        isActionButtonClickable.value = false
        if (accountNumber.valueOrEmpty.isBlank() || pin.valueOrEmpty.isBlank()) {
            onShowToast.value = R.string.error_field_empty.asStringValue
            isActionButtonClickable.value = true
            return
        }

        viewModelScope.launch {
            withProgress(loginProgress) {
                customerManager.getCustomerProfile(
                    accountNumber = accountNumber.valueOrEmpty,
                    pin = pin.valueOrEmpty
                ).onSuccess {
                    navigate(
                        navigationResId = R.id.accountFragment,
                        navOptions = NavOptions.Builder().apply {
                            setPopUpTo(R.id.driverFragment, false)
                        }.build()
                    )
                }.onFailure {
                    onShowToast.value = mapToInfoMessage(it)
                    isActionButtonClickable.value = true
                }
            }
        }
    }
}