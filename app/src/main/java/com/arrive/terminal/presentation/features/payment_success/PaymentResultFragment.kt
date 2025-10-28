
package com.arrive.terminal.presentation.features.payment_success;

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Parcelable
import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.os.bundleOf
import androidx.core.os.postDelayed
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.arrive.terminal.R
import com.arrive.terminal.core.model.Constants
import com.arrive.terminal.core.ui.base.BaseVMFragment
import com.arrive.terminal.core.ui.base.Inflate
import com.arrive.terminal.core.ui.extensions.onClickSafe
import com.arrive.terminal.core.ui.extensions.parcelableOrException
import com.arrive.terminal.core.ui.extensions.textOrInvisibleIfBlank
import com.arrive.terminal.core.ui.model.StringValue
import com.arrive.terminal.databinding.FragmentPaymentResultBinding
import com.arrive.terminal.domain.manager.StringsManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.parcelize.Parcelize
import javax.inject.Inject

enum class PaymentResultType(
    @StringRes val titleRes: Int,
    @DrawableRes val iconRes: Int,
    @DrawableRes val gradientId: Int,
) {

    SUCCESS(
        R.string.success_payment_successful,
        R.drawable.ic_success,
        R.drawable.circular_gradient_green
    ),
    ERROR(R.string.success_payment_error, R.drawable.ic_failure, R.drawable.circular_gradient_red),
    DECLINE(
        R.string.success_payment_declined,
        R.drawable.ic_failure,
        R.drawable.circular_gradient_grey
    );
}

@Parcelize
class PaymentResultData(
    val type: PaymentResultType,
    val priceFormatted: String,
    val message: StringValue?,
    val accountId: String?,
    val driverId: String?,
    val isRateEnabled: Boolean?,
    val defaultRate: Int?
) : Parcelable

@AndroidEntryPoint
class PaymentResultFragment : BaseVMFragment<FragmentPaymentResultBinding, PaymentResultViewModel>() {

    private var mediaPlayer: MediaPlayer? = null

    override val inflate: Inflate<FragmentPaymentResultBinding> = FragmentPaymentResultBinding::inflate

    override val viewModel by viewModels<PaymentResultViewModel>()

    private val data by parcelableOrException<PaymentResultData>(DATA_KEY)

    private val returnBackHandler = Handler(Looper.getMainLooper())

    private var ratingDialog: AlertDialog? = null

    @Inject
    lateinit var stringsManager: StringsManager

    override fun FragmentPaymentResultBinding.initUI(savedInstanceState: Bundle?) {
        var selectedRating = data.defaultRate ?: 5
        binding.apply {
            rateTitle.text = stringsManager.getString(
                Constants.PAYMENT_RESULT_RATE_TITLE,
                requireContext().getString(R.string.payment_result_rate_title)
            )
            poweredBy.text = stringsManager.getString(
                Constants.PAYMENT_RESULT_POWERED_BY,
                requireContext().getString(R.string.payment_result_powered_by)
            )
            tryAgain.setText(
                stringsManager.getString(
                    Constants.COMMON_TRY_AGAIN,
                    requireContext().getString(R.string.common_try_again)
                )
            )
            iconSuccess.setImageResource(data.type.iconRes)
            title.setText(data.type.titleRes)
            topBackground.setImageDrawable(
                AppCompatResources.getDrawable(
                    requireContext(),
                    data.type.gradientId
                )
            )
            disclaimer.text = getString(R.string.payment_result_disclaimer, RETURN_BACK_DELAY_SECONDS.toString())
            price.text = data.priceFormatted
            message.textOrInvisibleIfBlank(data.message?.asString(requireContext()))
            poweredBy.isVisible = data.type == PaymentResultType.SUCCESS
            tryAgain.isVisible = data.type != PaymentResultType.SUCCESS

            tryAgain.onClickSafe { viewModel.navigateBack() }

            rateContainer.isVisible =
                data.type == PaymentResultType.SUCCESS && data.isRateEnabled == true

            val starViews = listOf(star1, star2, star3, star4, star5)

            fun updateStars(rating: Int) {
                for (i in 0 until 5) {
                    starViews[i].setImageResource(
                        if (i < rating) R.drawable.ic_star_filled else R.drawable.ic_star_outline
                    )
                }
            }

            updateStars(selectedRating)

            for ((index, star) in starViews.withIndex()) {
                star.onClickSafe {
                    selectedRating = index + 1
                    updateStars(selectedRating)
                }
            }
        }

        returnBackHandler.postDelayed(RETURN_BACK_DELAY_SECONDS * 1000L) {
            viewModel.backToDriverPage(selectedRating, data.driverId, data.accountId)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (data.type == PaymentResultType.SUCCESS) {
            mediaPlayer = MediaPlayer.create(requireContext(), R.raw.ride_payment)
            mediaPlayer?.start()
        } else if (data.type == PaymentResultType.ERROR) {
            mediaPlayer = MediaPlayer.create(requireContext(), R.raw.brass_fail)
            mediaPlayer?.start()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        ratingDialog?.dismiss()
        ratingDialog = null
        mediaPlayer?.release()
        mediaPlayer = null
        returnBackHandler.removeCallbacksAndMessages(null)
    }

    override fun PaymentResultViewModel.observeViewModel() {
        progress.observe(viewLifecycleOwner) {
            setHostProgress(it)
        }
    }

    companion object {

        private const val RETURN_BACK_DELAY_SECONDS = 10L
        private const val DATA_KEY = "DATA_KEY"

        fun getBundle(
            type: PaymentResultType,
            priceFormatted: String,
            message: StringValue?,
            accountId: String?,
            driverId: String?,
            isRateEnabled: Boolean?,
            defaultRate: Int?
        ) = bundleOf(
            DATA_KEY to PaymentResultData(
                type,
                priceFormatted,
                message,
                accountId,
                driverId,
                isRateEnabled,
                defaultRate
            )
        )
    }
}
