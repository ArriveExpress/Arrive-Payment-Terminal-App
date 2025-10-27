package com.arrive.terminal.presentation.features.payment_method;

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavOptions
import com.arrive.terminal.R
import com.arrive.terminal.core.ui.base.BaseViewModel
import com.arrive.terminal.core.ui.extensions.getOrException
import com.arrive.terminal.core.ui.extensions.parcelableOrNull
import com.arrive.terminal.core.ui.extensions.valueOrEmpty
import com.arrive.terminal.core.ui.livedata.NotNullMutableLiveData
import com.arrive.terminal.core.ui.livedata.SafeLiveEvent
import com.arrive.terminal.core.ui.model.StringValue
import com.arrive.terminal.core.ui.model.StringValue.Companion.asStringValue
import com.arrive.terminal.core.ui.utils.getPriceFormatted
import com.arrive.terminal.core.ui.utils.withProgress
import com.arrive.terminal.domain.manager.DriverManager
import com.arrive.terminal.domain.model.AccountModel
import com.arrive.terminal.domain.model.CardModel
import com.arrive.terminal.domain.model.PayingTerminalEventModel
import com.arrive.terminal.domain.model.flaggedTrip
import com.arrive.terminal.domain.model.ride
import com.arrive.terminal.presentation.features.enter_card.EnterCardFragment
import com.arrive.terminal.presentation.features.enter_card.EnterCardResult
import com.arrive.terminal.presentation.features.payment_method.PaymentMethodFragment.Companion.PAYING_EVENT_ARG_KEY
import com.arrive.terminal.presentation.features.payment_success.PaymentResultFragment
import com.arrive.terminal.presentation.features.payment_success.PaymentResultType
import com.example.card_payment.CardReadInfo
import com.example.card_payment.TransResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentMethodViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val driverManager: DriverManager
): BaseViewModel() {

    val accountNumber = MutableLiveData<String>()
    val pin = MutableLiveData<String>()

    val eventModelLiveData = NotNullMutableLiveData(
        savedStateHandle.getOrException<PayingTerminalEventModel>(PAYING_EVENT_ARG_KEY)
    )

    val hostProgress = NotNullMutableLiveData(false)
    val onPayWithCard = SafeLiveEvent<CardReadInfo>()

    private val eventModel get() = eventModelLiveData.value

    fun onPayWithCardClick() {
        onPayWithCard.value = CardReadInfo(getPriceFormatted(eventModel.amount, withFee = true))
    }

    fun payWithAccount() {
        if (accountNumber.valueOrEmpty.isBlank() || pin.valueOrEmpty.isBlank()) {
            onShowToast.value = R.string.error_field_empty.asStringValue
            return
        }

        viewModelScope.launch {
            onClearFocusHideKeyboard.fire()
            withProgress(hostProgress) {
                driverManager.processAccountPayment(
                    ride = eventModel.ride,
                    flaggedTrip = eventModel.flaggedTrip,
                    account = AccountModel(accountNumber.valueOrEmpty, pin.valueOrEmpty)
                ).onSuccess {
                    toPaymentResult(cardPayment = false, PaymentResultType.SUCCESS)
                }.onFailure {
                    toPaymentResult(cardPayment = false, PaymentResultType.ERROR, mapToInfoMessage(it))
                }
            }
        }
    }

    fun handlePayWithCard(result: TransResult) {
        if (result.manual) {
            navigate(
                navigationResId = R.id.enterCardFragment,
                args = EnterCardFragment.getBundle(
                    title = "Pay ${getPriceFormatted(eventModel.amount, withFee = true)}",
                    titleSize = 40f
                ),
                requestKey = EnterCardFragment.CARD_REQUEST_KEY,
                resultListener = { _, bundle ->
                    bundle.parcelableOrNull<EnterCardResult>(EnterCardFragment.CARD_RESULT_KEY)?.let {
                        payWithCard(
                            result = CardModel(
                                number = it.cardNumber,
                                cardExpireMonth = it.expMonth,
                                cardExpireYear = it.expYear
                            )
                        )
                    }
                }
            )
        } else {
            payWithCard(
                result = CardModel(
                    number = result.cardNumber,
                    cardExpireMonth = result.expiryMonth.toString(),
                    cardExpireYear = result.expireYear.toString()
                )
            )
        }
    }

    private fun payWithCard(result: CardModel) {
        viewModelScope.launch {
            withProgress(hostProgress) {
                driverManager.processCardPayment(
                    ride = eventModel.ride,
                    flaggedTrip = eventModel.flaggedTrip,
                    card = CardModel(
                        number = result.number,
                        cardExpireMonth = result.cardExpireMonth,
                        cardExpireYear = result.cardExpireYear,
                    )
                ).onSuccess {
                    toPaymentResult(cardPayment = true, PaymentResultType.SUCCESS)
                }.onFailure {
                    toPaymentResult(cardPayment = true, PaymentResultType.ERROR, mapToInfoMessage(it))
                }
            }
        }
    }

    private fun toPaymentResult(
        cardPayment: Boolean,
        type: PaymentResultType,
        message: StringValue? = null
    ) {
        navigate(
            navigationResId = R.id.paymentSuccessFragment,
            args = PaymentResultFragment.getBundle(
                type = type,
                priceFormatted = getPriceFormatted(eventModel.amount, withFee = cardPayment),
                message = message,
            ),
            navOptions = NavOptions.Builder().apply {
                val popupTo = if (type == PaymentResultType.SUCCESS) R.id.driverFragment else R.id.paymentMethodFragment
                setPopUpTo(popupTo, false)
            }.build()
        )
    }
}