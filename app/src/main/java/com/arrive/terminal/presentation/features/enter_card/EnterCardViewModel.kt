package com.arrive.terminal.presentation.features.enter_card;

import LiveEvent
import androidx.lifecycle.MutableLiveData
import com.arrive.terminal.R
import com.arrive.terminal.core.ui.base.BaseViewModel
import com.arrive.terminal.core.ui.extensions.valueOrEmpty
import com.arrive.terminal.core.ui.livedata.SafeLiveEvent
import com.arrive.terminal.core.ui.model.StringValue.Companion.asStringValue
import com.arrive.terminal.core.ui.utils.isValidExpiryDateFormat
import com.arrive.terminal.presentation.adapter.CreditCardItem
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EnterCardViewModel @Inject constructor() : BaseViewModel() {

    val cardNumber = MutableLiveData<String>()
    val exp = MutableLiveData<String>()

    val onCloseWithManualResult = LiveEvent()
    val onCloseWithSelectedResult = SafeLiveEvent<String>()

    fun onContinueClick() {
        if (cardNumber.valueOrEmpty.isBlank() || !isValidExpiryDateFormat(exp.valueOrEmpty)) {
            onShowToast.value = R.string.error_field_empty.asStringValue
            return
        }

        onCloseWithManualResult.fire()
    }

    fun onCardSelected(item: CreditCardItem) {
        onCloseWithSelectedResult.value = item.id
    }
}