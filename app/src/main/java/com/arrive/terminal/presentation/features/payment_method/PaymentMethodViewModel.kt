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
    val isActionButtonClickable = MutableLiveData(true)

    val eventModelLiveData = NotNullMutableLiveData(
        savedStateHandle.getOrException<PayingTerminalEventModel>(PAYING_EVENT_ARG_KEY)
    )

    val hostProgress = NotNullMutableLiveData(false)
    val onPayWithCard = SafeLiveEvent<CardReadInfo>()

    private val eventModel get() = eventModelLiveData.value

    fun onPayWithCardClick() {
        viewModelScope.launch {
            withProgress(hostProgress) {
                val feeFixed = driverManager.getFeeFixed(eventModel.driverId)
                val feeIndex = driverManager.getFeePercent(eventModel.driverId)
                if (feeFixed != null && feeIndex != null) {
                    val formattedAmount = getPriceFormatted(
                        amount = eventModel.amount,
                        withFee = true,
                        feeIndex = feeIndex,
                        additionalFee = feeFixed
                    )
                    onPayWithCard.value = CardReadInfo(formattedAmount)
                } else {
                    onShowToast.value = mapToInfoMessage(NullPointerException())
                }
            }
        }
    }

    fun payWithAccount() {
        isActionButtonClickable.value = false
        if (accountNumber.valueOrEmpty.isBlank() || pin.valueOrEmpty.isBlank()) {
            onShowToast.value = R.string.error_field_empty.asStringValue
            isActionButtonClickable.value = true
            return
        }

        viewModelScope.launch {
            onClearFocusHideKeyboard.fire()
            withProgress(hostProgress) {
                driverManager.processAccountPayment(
                    ride = eventModel.ride,
                    flaggedTrip = eventModel.flaggedTrip,
                    account = AccountModel(accountNumber.valueOrEmpty, pin.valueOrEmpty)
                ).onSuccess { accountId ->
                    toPaymentResult(
                        cardPayment = false,
                        type = PaymentResultType.SUCCESS,
                        driverId = eventModel.driverId,
                        accountId = accountId
                    )
                }.onFailure {
                    isActionButtonClickable.value = true
                    toPaymentResult(
                        cardPayment = false,
                        type = PaymentResultType.ERROR,
                        message = mapToInfoMessage(it)
                    )
                }
            }
        }
    }

    fun handlePayWithCard(result: TransResult) {
        viewModelScope.launch {
            withProgress(hostProgress) {
                if (result.manual) {
                    val feeFixed = driverManager.getFeeFixed(eventModel.driverId)
                    val feeIndex = driverManager.getFeePercent(eventModel.driverId)
                    if (feeFixed != null && feeIndex != null) {
                        navigate(
                            navigationResId = R.id.enterCardFragment,
                            args = EnterCardFragment.getBundle(
                                title = "Pay ${
                                    getPriceFormatted(
                                        eventModel.amount,
                                        withFee = true,
                                        feeIndex = feeIndex,
                                        additionalFee = feeFixed
                                    )
                                }",
                                titleSize = 40f
                            ),
                            requestKey = EnterCardFragment.CARD_REQUEST_KEY,
                            resultListener = { _, bundle ->
                                bundle.parcelableOrNull<EnterCardResult>(EnterCardFragment.CARD_RESULT_KEY)
                                    ?.let {
                                        payWithCard(
                                            result = CardModel(
                                                isManualEntry = true,
                                                number = it.cardNumber,
                                                cardExpireMonth = it.expMonth,
                                                cardExpireYear = it.expYear,
                                                cvc = it.cvc
                                            )
                                        )
                                    }
                            })
                    } else {
                        onShowToast.value = mapToInfoMessage(NullPointerException())
                    }
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
        }
    }

    private fun payWithCard(result: CardModel) {
        viewModelScope.launch {
            withProgress(hostProgress) {
                driverManager.processCardPayment(
                    ride = eventModel.ride,
                    flaggedTrip = eventModel.flaggedTrip,
                    card = result
                ).onSuccess { accountId ->
                    toPaymentResult(
                        cardPayment = true,
                        type = PaymentResultType.SUCCESS,
                        driverId = eventModel.driverId,
                        accountId = accountId
                    )
                }.onFailure {
                    toPaymentResult(
                        cardPayment = true,
                        type = PaymentResultType.ERROR,
                        message = mapToInfoMessage(it)
                    )
                }
            }
        }
    }

    private fun toPaymentResult(
        cardPayment: Boolean,
        type: PaymentResultType,
        message: StringValue? = null,
        accountId: String? = null,
        driverId: String? = null
    ) {
        viewModelScope.launch {
            withProgress(hostProgress) {
                if (cardPayment) {
                    val feeFixed = driverManager.getFeeFixed(driverId)
                    val feeIndex = driverManager.getFeePercent(driverId)
                    if (feeFixed != null && feeIndex != null) {
                        navigate(
                            navigationResId = R.id.paymentSuccessFragment,
                            args = PaymentResultFragment.getBundle(
                                type = type,
                                priceFormatted = getPriceFormatted(
                                    amount = eventModel.amount,
                                    withFee = cardPayment,
                                    feeIndex = feeIndex,
                                    additionalFee = feeFixed
                                ),
                                message = message,
                                accountId = accountId,
                                driverId = driverId,
                                isRateEnabled = driverManager.getIsRateEnabled(driverId),
                                defaultRate = driverManager.getDefaultRate(driverId)
                            ),
                            navOptions = NavOptions.Builder().apply {
                                val popupTo = if (type == PaymentResultType.SUCCESS) {
                                    R.id.driverFragment
                                } else {
                                    R.id.paymentMethodFragment
                                }
                                setPopUpTo(popupTo, false)
                            }.build()
                        )
                    } else {
                        onShowToast.value = mapToInfoMessage(NullPointerException())
                    }
                } else {
                    navigate(
                        navigationResId = R.id.paymentSuccessFragment,
                        args = PaymentResultFragment.getBundle(
                            type = type,
                            priceFormatted = getPriceFormatted(
                                amount = eventModel.amount,
                                withFee = cardPayment
                            ),
                            message = message,
                            accountId = accountId,
                            driverId = driverId,
                            isRateEnabled = driverManager.getIsRateEnabled(driverId),
                            defaultRate = driverManager.getDefaultRate(driverId)
                        ),
                        navOptions = NavOptions.Builder().apply {
                            val popupTo = if (type == PaymentResultType.SUCCESS) {
                                R.id.driverFragment
                            } else {
                                R.id.paymentMethodFragment
                            }
                            setPopUpTo(popupTo, false)
                        }.build()
                    )
                }
            }
        }
    }
}