package com.arrive.terminal.presentation.features.payment_method;

import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.arrive.terminal.R
import com.arrive.terminal.core.model.Constants
import com.arrive.terminal.core.ui.base.BaseVMFragment
import com.arrive.terminal.core.ui.base.Inflate
import com.arrive.terminal.core.ui.binding.bindTwoWays
import com.arrive.terminal.core.ui.extensions.disableSelectInsertText
import com.arrive.terminal.core.ui.extensions.dpToPx
import com.arrive.terminal.core.ui.extensions.onClickSafe
import com.arrive.terminal.core.ui.extensions.textOrGoneIfBlank
import com.arrive.terminal.core.ui.utils.getPriceFormatted
import com.arrive.terminal.databinding.FragmentPaymentMethodBinding
import com.arrive.terminal.domain.manager.StringsManager
import com.arrive.terminal.domain.model.PayingTerminalEventModel
import com.example.card_payment.TransContract
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PaymentMethodFragment : BaseVMFragment<FragmentPaymentMethodBinding, PaymentMethodViewModel>() {

    override val inflate: Inflate<FragmentPaymentMethodBinding> =
        FragmentPaymentMethodBinding::inflate

    @Inject
    lateinit var stringsManager: StringsManager

    override val viewModel by viewModels<PaymentMethodViewModel>()

    private val cardReadLauncher = registerForActivityResult(TransContract()) { result ->
        if (result != null) {
            viewModel.handlePayWithCard(result)
        }
    }

    override fun FragmentPaymentMethodBinding.initUI(savedInstanceState: Bundle?) {
        showBreakdown.text = stringsManager.getString(
            Constants.PAYMENT_METHOD_SHOW_BREAKDOWN,
            requireContext().getString(R.string.payment_method_show_breakdown)
        )
        totalTitle.text = stringsManager.getString(
            Constants.PAYMENT_METHOD_TOTAL,
            requireContext().getString(R.string.payment_method_total)
        )
        feesDescription.text = stringsManager.getString(
            Constants.PAYMENT_METHOD_FEES,
            requireContext().getString(R.string.payment_method_fees)
        )
        payWithCard.setText(
            stringsManager.getString(
                Constants.PAYMENT_METHOD_PAY_WITH_CARD,
                requireContext().getString(R.string.payment_method_pay_with_card)
            )
        )
        cancel.setText(
            stringsManager.getString(
                Constants.COMMON_CANCEL,
                requireContext().getString(R.string.common_cancel)
            )
        )
        doubleInput.apply {
            inputFirst.apply {
                setText("1")
                setSelection(text?.length ?: 0)
                hint = stringsManager.getString(
                    Constants.COMMON_HINT_ACCOUNT_NUMBER,
                    context.getString(R.string.common_hint_account_number)
                )
                inputType = InputType.TYPE_CLASS_NUMBER
                bindTwoWays(viewLifecycleOwner, viewModel.accountNumber)
                disableSelectInsertText()
                addTextChangedListener(object : TextWatcher {
                    var isUpdating = false

                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                    }

                    override fun afterTextChanged(s: Editable?) {
                        if (isUpdating) return

                        isUpdating = true

                        val original = s.toString()
                        val filtered = original.filter { it.isDigit() }

                        val newText = if (filtered.startsWith("1")) {
                            filtered.take(11)
                        } else {
                            "1" + filtered.take(10)
                        }

                        if (newText != original) {
                            setText(newText)
                            setSelection(newText.length)
                        }

                        isUpdating = false
                    }
                })
            }
            inputSeconds.apply {
                hint = stringsManager.getString(
                    Constants.COMMON_HINT_PIN,
                    context.getString(R.string.common_hint_pin)
                )
                inputType = InputType.TYPE_CLASS_NUMBER
                bindTwoWays(viewLifecycleOwner, viewModel.pin)
                disableSelectInsertText()
            }
            continueAction.isVisible = true
            continueAction.isClickable = true
            continueAction.onClickSafe {
                viewModel.payWithAccount()
            }
        }

        // listeners
        payWithCard.onClickSafe { viewModel.onPayWithCardClick() }
        cancel.onClickSafe { viewModel.navigateBack() }
        showBreakdown.onClickSafe {
            breakdownContainer.isVisible = true
            backgroundImageBig.isVisible = true
        }
    }

    override fun PaymentMethodViewModel.observeViewModel() {
        isActionButtonClickable.observe(viewLifecycleOwner){
            binding.doubleInput.continueAction.isClickable = it
        }
        eventModelLiveData.observe(viewLifecycleOwner) { handleEventModel(it) }
        hostProgress.observe(viewLifecycleOwner) { setHostProgress(it) }
        onPayWithCard.observe(viewLifecycleOwner) { cardReadLauncher.launch(it) }
    }

    private fun handleEventModel(model: PayingTerminalEventModel) = withBinding {
        val priceFormatted = getPriceFormatted(model.amount)
        price.text = priceFormatted
        priceTotal.text = priceFormatted
        pickUpInfo.textOrGoneIfBlank(model.address)
        showBreakdown.isVisible = model.breakdown?.isNotEmpty() == true
        setupBreakdown(model.breakdown ?: emptyList())
    }

    private fun setupBreakdown(items: List<PayingTerminalEventModel.BreakdownItem>) {
        items.forEach {
            TextView(requireContext()).apply {
                setTextAppearance(R.style.Text_Default_White)
                textSize = 12f
                text = it.title
            }.also {
                val lp = ViewGroup.MarginLayoutParams(WRAP_CONTENT, WRAP_CONTENT).apply {
                    topMargin = requireContext().dpToPx(4f)
                }
                binding.priceElements.addView(it, lp)
            }

            TextView(requireContext()).apply {
                setTextAppearance(R.style.Text_Default_White)
                textSize = 12f
                text = getPriceFormatted(it.value) + if (it.minutes != null) {
                    requireContext().getString(R.string.payment_method_breakdown_min, it.minutes)
                } else {
                    ""
                }

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