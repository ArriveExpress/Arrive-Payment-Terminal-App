package com.arrive.terminal.presentation.features.payment_method;

import android.os.Bundle
import android.text.InputType
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.arrive.terminal.R
import com.arrive.terminal.core.ui.base.BaseVMFragment
import com.arrive.terminal.core.ui.base.Inflate
import com.arrive.terminal.core.ui.binding.bindTwoWays
import com.arrive.terminal.core.ui.extensions.dpToPx
import com.arrive.terminal.core.ui.extensions.disableSelectInsertText
import com.arrive.terminal.core.ui.extensions.onClickSafe
import com.arrive.terminal.core.ui.extensions.textOrGoneIfBlank
import com.arrive.terminal.core.ui.utils.getPriceFormatted
import com.arrive.terminal.databinding.FragmentPaymentMethodBinding
import com.arrive.terminal.domain.model.PayingTerminalEventModel
import com.arrive.terminal.domain.model.isRide
import com.example.card_payment.TransContract
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PaymentMethodFragment : BaseVMFragment<FragmentPaymentMethodBinding, PaymentMethodViewModel>() {

    override val inflate: Inflate<FragmentPaymentMethodBinding> =
        FragmentPaymentMethodBinding::inflate

    override val viewModel by viewModels<PaymentMethodViewModel>()

    private val cardReadLauncher = registerForActivityResult(TransContract()) { result ->
        if (result != null) {
            viewModel.handlePayWithCard(result)
        }
    }

    override fun FragmentPaymentMethodBinding.initUI(savedInstanceState: Bundle?) {
        doubleInput.apply {
            inputFirst.apply {
                hint = context.getString(R.string.common_hint_account_number)
                inputType = InputType.TYPE_CLASS_NUMBER
                bindTwoWays(viewLifecycleOwner, viewModel.accountNumber)
                disableSelectInsertText()
            }
            inputSeconds.apply {
                hint = context.getString(R.string.common_hint_pin)
                inputType = InputType.TYPE_CLASS_NUMBER
                bindTwoWays(viewLifecycleOwner, viewModel.pin)
                disableSelectInsertText()
            }
            continueAction.isVisible = true
            continueAction.onClickSafe { viewModel.payWithAccount() }
        }

        // listeners
        payWithCard.onClickSafe { viewModel.onPayWithCardClick() }
        cancel.onClickSafe { viewModel.navigateBack() }
        showBreakdown.onClickSafe { breakdownContainer.isVisible = true }
    }

    override fun PaymentMethodViewModel.observeViewModel() {
        eventModelLiveData.observe(viewLifecycleOwner) { handleEventModel(it) }
        hostProgress.observe(viewLifecycleOwner) { setHostProgress(it) }
        onPayWithCard.observe(viewLifecycleOwner) { cardReadLauncher.launch(it) }
    }

    private fun handleEventModel(model: PayingTerminalEventModel) = withBinding {
        val priceFormatted = getPriceFormatted(model.amount)
        price.text = priceFormatted
        priceTotal.text = priceFormatted
        pickUpInfo.textOrGoneIfBlank(model.address)
        showBreakdown.isVisible = model.isRide
        setupBreakdown(model.breakdown)
    }

    private fun setupBreakdown(items: List<PayingTerminalEventModel.BreakdownItem>) {
        items.forEach {
            TextView(requireContext()).apply {
                setTextAppearance(R.style.Text_Default_White)
                textSize = 16f
                text = it.title
            }.also {
                val lp = ViewGroup.MarginLayoutParams(WRAP_CONTENT, WRAP_CONTENT).apply {
                    topMargin = requireContext().dpToPx(4f)
                }
                binding.priceElements.addView(it, lp)
            }

            TextView(requireContext()).apply {
                setTextAppearance(R.style.Text_Default_White)
                textSize = 16f
                text = getPriceFormatted(it.value)
            }.also {
                val lp = ViewGroup.MarginLayoutParams(WRAP_CONTENT, WRAP_CONTENT).apply {
                    topMargin = requireContext().dpToPx(4f)
                }
                binding.priceElementsValues.addView(it, lp)
            }
        }
    }

    companion object {

        const val PAYING_EVENT_ARG_KEY = "paying_event_arg_key"

        fun getBundle(event: PayingTerminalEventModel) = bundleOf(
            PAYING_EVENT_ARG_KEY to event
        )
    }
}