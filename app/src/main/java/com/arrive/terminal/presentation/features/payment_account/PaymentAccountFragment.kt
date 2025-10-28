package com.arrive.terminal.presentation.features.payment_account;

import android.os.Bundle
import android.text.InputType
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import com.arrive.terminal.R
import com.arrive.terminal.core.model.Constants
import com.arrive.terminal.core.ui.base.BaseVMFragment
import com.arrive.terminal.core.ui.base.Inflate
import com.arrive.terminal.core.ui.binding.bindTwoWays
import com.arrive.terminal.core.ui.extensions.disableSelectInsertText
import com.arrive.terminal.core.ui.extensions.onClickSafe
import com.arrive.terminal.core.ui.model.StringValue.Companion.asStringValue
import com.arrive.terminal.databinding.FragmentPaymentAccountBinding
import com.arrive.terminal.domain.manager.StringsManager
import com.arrive.terminal.domain.model.FlaggedTripModel
import com.arrive.terminal.domain.model.RideModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PaymentAccountFragment :
    BaseVMFragment<FragmentPaymentAccountBinding, PaymentAccountViewModel>() {

    override val inflate: Inflate<FragmentPaymentAccountBinding> =
        FragmentPaymentAccountBinding::inflate

    @Inject
    lateinit var stringsManager: StringsManager

    override val viewModel by viewModels<PaymentAccountViewModel>()

    override fun FragmentPaymentAccountBinding.initUI(savedInstanceState: Bundle?) {
        confirm.setText(
            stringsManager.getString(
                Constants.ACCOUNT_CONFIRM_PAYMENT,
                requireContext().getString(R.string.account_confirm_payment)
            )
        )
        back.setText(
            stringsManager.getString(
                Constants.ACCOUNT_BACK,
                requireContext().getString(R.string.account_back)
            )
        )
        setTitle(
            stringsManager.getString(
                Constants.ACCOUNT_PAYMENT,
                requireContext().getString(R.string.account_payment)
            ).asStringValue
        )
        inputNumber.editText.hint = stringsManager.getString(
            Constants.ACCOUNT_NUMBER_HINT,
            requireContext().getString(R.string.account_number_hint)
        )
        inputNumber.editText.bindTwoWays(viewLifecycleOwner, viewModel.number)
        inputNumber.editText.inputType = InputType.TYPE_CLASS_NUMBER
        inputNumber.editText.disableSelectInsertText()
        inputPin.editText.hint = stringsManager.getString(
            Constants.ACCOUNT_NUMBER_PIN,
            requireContext().getString(R.string.account_number_pin)
        )
        inputPin.editText.bindTwoWays(viewLifecycleOwner, viewModel.pin)
        inputPin.editText.inputType = InputType.TYPE_CLASS_NUMBER
        inputPin.editText.disableSelectInsertText()

        confirm.onClickSafe { viewModel.onConfirmClick() }
        back.onClickSafe { viewModel.navigateBack() }
    }

    override fun PaymentAccountViewModel.observeViewModel() {
        hostProgress.observe(viewLifecycleOwner) { setHostProgress(it) }
    }

    companion object {

        const val KEY_FLAGGED_MODEL = "KEY_FLAGGED_MODEL"
        const val KEY_RIDE_MODEL = "KEY_RIDE_MODEL"

        fun getBundle(
            selectedRide: RideModel?,
            flaggedTrip: FlaggedTripModel?
        ): Bundle {
            return bundleOf(
                KEY_RIDE_MODEL to selectedRide,
                KEY_FLAGGED_MODEL to flaggedTrip
            )
        }
    }
}