package com.arrive.terminal.presentation.features.account;

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.arrive.terminal.R
import com.arrive.terminal.core.ui.base.BaseViewModel
import com.arrive.terminal.core.ui.extensions.parcelableOrNull
import com.arrive.terminal.core.ui.extensions.updateIfNew
import com.arrive.terminal.core.ui.model.StringValue
import com.arrive.terminal.core.ui.utils.AMOUNT_ACCOUNT_REFILL_CHARGE_INDEX
import com.arrive.terminal.core.ui.utils.getPriceFormatted
import com.arrive.terminal.core.ui.utils.withProgress
import com.arrive.terminal.domain.manager.CustomerManager
import com.arrive.terminal.domain.model.CreditCardModel
import com.arrive.terminal.domain.model.CustomerAccountModel
import com.arrive.terminal.presentation.adapter.CreditCardItem
import com.arrive.terminal.presentation.features.enter_card.EnterCardFragment
import com.arrive.terminal.presentation.features.enter_card.EnterCardResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val customerManager: CustomerManager
) : BaseViewModel() {

    val progress = MutableLiveData<Boolean>()
    val customer = MutableLiveData<CustomerAccountModel>()

    fun loadCustomer() {
        viewModelScope.launch {
            withProgress(progress) {
                customerManager.fetchAuthorizedCustomer()
                    .onSuccess { customer.value = it }
                    .onFailure { onShowToast.value = mapToInfoMessage(it) }
            }
        }
    }

    fun onRefillClick(amount: Double) {
        val totalAmountFormatted = getPriceFormatted(
            amount = amount,
            withFee = true,
            feeIndex = AMOUNT_ACCOUNT_REFILL_CHARGE_INDEX
        )
        navigateToSelectCard(
            title = "$totalAmountFormatted\nSelect card or enter new",
            cards = customer.value?.cards.orEmpty(),
            totalAmountFormatted = totalAmountFormatted
        ) {
            refillAccount(amount, it)
        }
    }

    fun onCardSelected(cardItem: CreditCardItem) {
        val customerValue = customer.value ?: return

        viewModelScope.launch {
            withProgress(progress) {
                customerManager.setDefaultCard(customerValue.id, cardItem.id)
                    .onSuccess {
                        customer.updateIfNew {
                            it.copy(
                                cards = it.cards.map { card ->
                                    card.copy(defaultCard = card.id == cardItem.id)
                                }
                            )
                        }
                    }
                    .onFailure { onShowToast.value = mapToInfoMessage(it) }
            }
        }
    }

    fun onAddNewCardClick() {
        navigateToSelectCard("Add new card") {
            addNewCard(it)
        }
    }

    fun onLogoutClick() {
        customerManager.clear()
        navigateBack()
    }

    private fun navigateToSelectCard(
        title: String,
        totalAmountFormatted: String? = null,
        cards: List<CreditCardModel> = emptyList(),
        onCardSelected: (EnterCardResult) -> Unit
    ) {
        navigate(
            navigationResId = R.id.enterCardFragment,
            args = EnterCardFragment.getBundle(
                title = title,
                titleSize = 27f,
                cards = cards,
                tapOrInsertVisible = true,
                formattedPrice = totalAmountFormatted,
            ),
            requestKey = EnterCardFragment.CARD_REQUEST_KEY,
            resultListener = { _, result ->
                result.parcelableOrNull<EnterCardResult>(EnterCardFragment.CARD_RESULT_KEY)?.let {
                    onCardSelected.invoke(it)
                }
            }
        )
    }

    private fun refillAccount(amount: Double, card: EnterCardResult) {
        val customer = customer.value ?: return

        viewModelScope.launch {
            withProgress(progress) {
                customerManager.addBalance(
                    customerId = customer.id,
                    amount = amount,
                    cardId = card.cardId,
                    cardNumber = card.cardNumber,
                    cardExpMonth = card.expMonth,
                    cardExpYear = card.expYear
                ).onSuccess {
                    onShowToast.value = StringValue.stringResource(
                        R.string.account_refill_success_message,
                        getPriceFormatted(amount)
                    )
                    loadCustomer()
                }.onFailure {
                    onShowToast.value = mapToInfoMessage(it)
                }
            }
        }
    }

    private fun addNewCard(card: EnterCardResult) {
        val customerValue = customer.value ?: return

        viewModelScope.launch {
            withProgress(progress) {
                customerManager.saveCreditCard(
                    customerId = customerValue.id,
                    cardNumber = card.cardNumber,
                    expiryMonth = card.expMonth,
                    expiryYear = card.expYear
                ).onSuccess {
                    loadCustomer()
                }.onFailure {
                    onShowToast.value = mapToInfoMessage(it)
                }
            }
        }
    }
}