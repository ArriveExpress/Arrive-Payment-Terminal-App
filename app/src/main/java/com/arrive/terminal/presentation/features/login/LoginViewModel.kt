package com.arrive.terminal.presentation.features.login;

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.arrive.terminal.R
import com.arrive.terminal.core.ui.base.BaseViewModel
import com.arrive.terminal.core.ui.extensions.valueOrEmpty
import com.arrive.terminal.core.ui.model.StringValue.Companion.asStringValue
import com.arrive.terminal.core.ui.utils.withProgress
import com.arrive.terminal.domain.manager.DriverManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val driverManager: DriverManager
) : BaseViewModel() {

    val driverId = MutableLiveData<String>()
    val loginProgress = MutableLiveData<Boolean>()

    fun onLoginClick() {
//        navigate(
//            navigationResId = R.id.accountLoginFragment
//        )
        val id = driverId.valueOrEmpty
        if (id.isBlank()) {
            onShowToast.value = R.string.error_field_empty.asStringValue
            return
        }

        viewModelScope.launch {
            onClearFocusHideKeyboard.fire()
            withProgress(loginProgress) {
                driverManager.getMainScreen(driverId.valueOrEmpty)
                    .onSuccess {
                        driverManager.saveDriverId(id)
                        navigate(
                            navigationResId = R.id.driverFragment,
                            asStartDestination = true
                        )
                    }
                    .onFailure { onShowToast.value = mapToInfoMessage(it) }
            }
        }
    }
}