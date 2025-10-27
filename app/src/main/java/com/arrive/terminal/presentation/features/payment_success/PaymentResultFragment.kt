package com.arrive.terminal.presentation.features.payment_success;

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Parcelable
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.os.bundleOf
import androidx.core.os.postDelayed
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.arrive.terminal.R
import com.arrive.terminal.core.ui.base.BaseVMFragment
import com.arrive.terminal.core.ui.base.Inflate
import com.arrive.terminal.core.ui.extensions.applyTint
import com.arrive.terminal.core.ui.extensions.onClickSafe
import com.arrive.terminal.core.ui.extensions.parcelableOrException
import com.arrive.terminal.core.ui.extensions.textOrInvisibleIfBlank
import com.arrive.terminal.core.ui.model.StringValue
import com.arrive.terminal.databinding.FragmentPaymentResultBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.parcelize.Parcelize

enum class PaymentResultType(
    @StringRes val titleRes: Int,
    @DrawableRes val iconRes: Int,
    @ColorRes val colorRes: Int,
) {

    SUCCESS(R.string.success_payment_successful, R.drawable.ic_success, R.color.paymentSuccess),
    ERROR(R.string.success_payment_error, R.drawable.ic_failure, R.color.paymentError),
    DECLINE(R.string.success_payment_declined, R.drawable.ic_failure, R.color.paymentDecline);
}

@Parcelize
class PaymentResultData(
    val type: PaymentResultType,
    val priceFormatted: String,
    val message: StringValue?
) : Parcelable

@AndroidEntryPoint
class PaymentResultFragment : BaseVMFragment<FragmentPaymentResultBinding, PaymentResultViewModel>() {

    override val inflate: Inflate<FragmentPaymentResultBinding> = FragmentPaymentResultBinding::inflate

    override val viewModel by viewModels<PaymentResultViewModel>()

    private val data by parcelableOrException<PaymentResultData>(DATA_KEY)

    private val returnBackHandler = Handler(Looper.getMainLooper())

    override fun FragmentPaymentResultBinding.initUI(savedInstanceState: Bundle?) {
        binding.apply {
            iconSuccess.setImageResource(data.type.iconRes)
            title.setText(data.type.titleRes)
            topBackground.setImageDrawable(topBackground.drawable?.applyTint(requireContext().getColor(data.type.colorRes)))
            disclaimer.text = getString(R.string.payment_result_disclaimer, RETURN_BACK_DELAY_SECONDS.toString())
            price.text = data.priceFormatted
            message.textOrInvisibleIfBlank(data.message?.asString(requireContext()))
            poweredBy.isVisible = data.type == PaymentResultType.SUCCESS
            tryAgain.isVisible = data.type != PaymentResultType.SUCCESS

            tryAgain.onClickSafe { viewModel.navigateBack() }
        }

        returnBackHandler.postDelayed(RETURN_BACK_DELAY_SECONDS * 1000L) {
            viewModel.toDriverPage()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        returnBackHandler.removeCallbacksAndMessages(null)
    }

    companion object {

        private const val RETURN_BACK_DELAY_SECONDS = 10L
        private const val DATA_KEY = "DATA_KEY"

        fun getBundle(
            type: PaymentResultType,
            priceFormatted: String,
            message: StringValue?
        ) = bundleOf(DATA_KEY to PaymentResultData(type, priceFormatted, message))
    }
}