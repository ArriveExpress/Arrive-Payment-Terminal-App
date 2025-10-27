package com.arrive.terminal.presentation.features.enter_card;

import android.os.Bundle
import android.os.Parcelable
import android.text.InputType
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import com.arrive.terminal.R
import com.arrive.terminal.core.ui.base.BaseVMFragment
import com.arrive.terminal.core.ui.base.Inflate
import com.arrive.terminal.core.ui.binding.bindTwoWays
import com.arrive.terminal.core.ui.extensions.disableSelectInsertText
import com.arrive.terminal.core.ui.extensions.onClickSafe
import com.arrive.terminal.core.ui.extensions.parcelableOrException
import com.arrive.terminal.core.ui.extensions.setupAsExpiryDateField
import com.arrive.terminal.core.ui.extensions.textOrEmpty
import com.arrive.terminal.core.ui.utils.getExpiryDateRaw
import com.arrive.terminal.databinding.FragmentEnterCardBinding
import com.arrive.terminal.domain.model.CreditCardModel
import com.arrive.terminal.presentation.adapter.CreditCardItem
import com.arrive.terminal.presentation.adapter.creditCardAdapterDelegate
import com.example.card_payment.CardReadInfo
import com.example.card_payment.TransContract
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.parcelize.Parcelize

@Parcelize
data class EnterCardData(
    val title: String,
    val titleSize: Float,
    val creditCards: List<CreditCardModel>?,
    val tapOrInsertVisible: Boolean,
    val formattedPrice: String?,
) : Parcelable

@Parcelize
data class EnterCardResult(
    val cardId: String?,
    val cardNumber: String,
    val expMonth: String,
    val expYear: String,
) : Parcelable {

    companion object {

        fun createDefault(
            cardId: String? = null,
            cardNumber: String = "",
            expMonth: String = "",
            expYear: String = ""
        ) = EnterCardResult(cardId, cardNumber, expMonth, expYear)
    }
}

@AndroidEntryPoint
class EnterCardFragment : BaseVMFragment<FragmentEnterCardBinding, EnterCardViewModel>() {

    override val inflate: Inflate<FragmentEnterCardBinding> = FragmentEnterCardBinding::inflate

    override val viewModel by viewModels<EnterCardViewModel>()

    private val data by parcelableOrException<EnterCardData>(DATA_ARG_KEY)

    private val tapOrInsertCardLaunch = registerForActivityResult(TransContract()) { result ->
        if (result != null) {
            closeWithResult(result = EnterCardResult.createDefault(
                cardNumber = result.cardNumber,
                expMonth = result.expiryMonth.toString(),
                expYear = result.expireYear.toString()
            ))
        }
    }

    private val cardsAdapter = ListDelegationAdapter(
        creditCardAdapterDelegate {
            viewModel.onCardSelected(it)
        }
    )

    override fun FragmentEnterCardBinding.initUI(savedInstanceState: Bundle?) {
        title.text = data.title
        title.textSize = data.titleSize
        cards.adapter = cardsAdapter
        data.creditCards?.let { setupCards(it) }
        tapOrInsert.isVisible = data.tapOrInsertVisible

        doubleInput.apply {
            inputFirst.apply {
                hint = context.getString(R.string.common_hint_credit_card)
                inputType = InputType.TYPE_CLASS_NUMBER
                bindTwoWays(viewLifecycleOwner, viewModel.cardNumber)
                disableSelectInsertText()
            }
            inputSeconds.apply {
                hint = context.getString(R.string.common_hint_card_exp)
                inputType = InputType.TYPE_CLASS_NUMBER
                bindTwoWays(viewLifecycleOwner, viewModel.exp)
                setupAsExpiryDateField()
                disableSelectInsertText()
            }
            continueAction.isVisible = true
        }

        // listeners
        cancel.onClickSafe { viewModel.navigateBack() }
        doubleInput.continueAction.onClickSafe { viewModel.onContinueClick() }
        tapOrInsert.onClickSafe {
            tapOrInsertCardLaunch.launch(CardReadInfo(amount = data.formattedPrice))
        }
    }

    override fun EnterCardViewModel.observeViewModel() {
        onCloseWithManualResult.observe(viewLifecycleOwner) {
            binding.doubleInput.apply {
                val cardExp = inputSeconds.textOrEmpty.getExpiryDateRaw() ?: return@observe
                closeWithResult(
                    result = EnterCardResult.createDefault(
                        cardNumber = inputFirst.textOrEmpty,
                        expMonth = cardExp.first.toString(),
                        expYear = cardExp.second.toString()
                    )
                )
            }
        }
        onCloseWithSelectedResult.observe(viewLifecycleOwner) {
            closeWithResult(EnterCardResult.createDefault(cardId = it))
        }
    }

    private fun closeWithResult(result: EnterCardResult) {
        setFragmentResult(CARD_REQUEST_KEY, bundleOf(
            CARD_RESULT_KEY to result
        ))
        viewModel.navigateBack()
    }

    private fun setupCards(cards: List<CreditCardModel>) {
        binding.cards.isVisible = cards.isNotEmpty()
        cardsAdapter.items = cards.mapIndexed { index, card ->
            CreditCardItem(
                id = card.id,
                lastFour = card.lastFour,
                default = false,
                drawSelector = false,
                drawBottomDivider = index != cards.lastIndex
            )
        }
    }

    companion object {

        const val CARD_REQUEST_KEY = "card_request_key"
        const val CARD_RESULT_KEY = "card_result_key"
        const val DATA_ARG_KEY = "data_arg_key"

        fun getBundle(
            title: String,
            titleSize: Float,
            cards: List<CreditCardModel>? = null,
            tapOrInsertVisible: Boolean = false,
            formattedPrice: String? = null
        ) = bundleOf(
            DATA_ARG_KEY to EnterCardData(title, titleSize, cards, tapOrInsertVisible, formattedPrice)
        )
    }
}