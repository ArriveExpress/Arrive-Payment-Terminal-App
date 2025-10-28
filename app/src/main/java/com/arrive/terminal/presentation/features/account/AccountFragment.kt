package com.arrive.terminal.presentation.features.account;

import android.annotation.SuppressLint
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ListView
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.arrive.terminal.R
import com.arrive.terminal.core.model.Constants
import com.arrive.terminal.core.ui.base.BaseVMFragment
import com.arrive.terminal.core.ui.base.Inflate
import com.arrive.terminal.core.ui.extensions.disableSelectInsertText
import com.arrive.terminal.core.ui.extensions.onClickSafe
import com.arrive.terminal.core.ui.extensions.underline
import com.arrive.terminal.core.ui.helper.KeyboardHelper.clearFocusHideKeyboard
import com.arrive.terminal.core.ui.helper.KeyboardHelper.showKeyboard
import com.arrive.terminal.core.ui.utils.formatPrice
import com.arrive.terminal.databinding.FragmentAccountBinding
import com.arrive.terminal.domain.manager.StringsManager
import com.arrive.terminal.domain.model.CreditCardModel
import com.arrive.terminal.presentation.adapter.CreditCardItem
import com.arrive.terminal.presentation.adapter.PointerPopupAdapter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AccountFragment : BaseVMFragment<FragmentAccountBinding, AccountViewModel>() {

    override val inflate: Inflate<FragmentAccountBinding> = FragmentAccountBinding::inflate

    override val viewModel by viewModels<AccountViewModel>()

    @Inject
    lateinit var stringsManager: StringsManager

    private lateinit var cardAdapter: PointerPopupAdapter
    private lateinit var popupWindow: PointerPopupWindow

    private var mediaPlayer: MediaPlayer? = null

    override fun FragmentAccountBinding.initUI(savedInstanceState: Bundle?) {
        setupLoading()
        addNew.underline()

        val wm = getSystemService(requireContext(), WindowManager::class.java)
        val width = wm?.defaultDisplay?.width ?: ViewGroup.LayoutParams.WRAP_CONTENT

        popupWindow = PointerPopupWindow(requireContext(), width)

        val listView = ListView(requireContext()).apply {
            divider = ColorDrawable(ContextCompat.getColor(requireContext(), R.color.textGray))
            dividerHeight = 1
        }
        cardAdapter = PointerPopupAdapter(requireContext(), mutableListOf()) {
            popupWindow.dismiss()
            viewModel.onCardSelected(it)
        }
        listView.adapter = cardAdapter

        popupWindow.contentView = listView

        binding.cardDefault.onClickSafe {
            popupWindow.showAsPointer(binding.down, cardAdapter.count)
        }
        milesTitle.text = stringsManager.getString(
            Constants.ACCOUNT_ARRIVE_MILES,
            requireContext().getString(R.string.account_arrive_miles)
        )
        refillTitle.text = stringsManager.getString(
            Constants.ACCOUNT_REFILL_ACCOUNT,
            requireContext().getString(R.string.account_refill_account)
        )
        cardsTitle.text = stringsManager.getString(
            Constants.ACCOUNT_ARRIVE_WALLET,
            requireContext().getString(R.string.account_arrive_wallet)
        )
        addNew.text = stringsManager.getString(
            Constants.ACCOUNT_ADD_NEW,
            requireContext().getString(R.string.account_add_new)
        )
        defaultLabel.text = stringsManager.getString(
            Constants.ITEM_CARD_DEFAULT,
            requireContext().getString(R.string.item_card_default)
        )
        variantOther.setText(
            stringsManager.getString(
                Constants.ACCOUNT_PRICE_OTHER,
                requireContext().getString(R.string.account_price_other)
            )
        )
        addNew.onClickSafe { viewModel.onAddNewCardClick() }
        logout.onClickSafe { viewModel.onLogoutClick() }
        refill.onClickSafe {
            viewModel.onRefillClick(
                amount = when {
                    variant50.isSelected -> 50.0
                    variant75.isSelected -> 75.0
                    variant100.isSelected -> 100.0
                    variantOtherLabel.isSelected -> variantOther.text?.toString()?.toDoubleOrNull()
                        ?: 0.0
                    else -> return@onClickSafe
                }
            )
            activity?.window?.clearFocusHideKeyboard(binding.root)
        }

        variant100.isSelected = true
        variantOther.disableSelectInsertText()

        // setup variants
        val refillVariants = listOf(variant50, variant75, variant100, variantOtherLabel)
        refillVariants.forEach { view ->
            view.setOnClickListener {
                refillVariants.forEach { it.isSelected = false }
                view.isSelected = true

                variantOther.isSelected = view == variantOtherLabel
                if (view == variantOtherLabel) {
                    activity?.window?.showKeyboard(variantOther)
                }

                if (view != variantOtherLabel) {
                    activity?.window?.clearFocusHideKeyboard(binding.root)
                }
            }
        }
        variantOther.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                variantOther.text = null
                variantOtherLabel.performClick()
            } else {
                variantOther.setText(
                    stringsManager.getString(
                        Constants.ACCOUNT_PRICE_OTHER,
                        "Other"
                    )
                )
            }
        }

        viewModel.loadCustomer()
    }

    override fun AccountViewModel.observeViewModel() {
        onSuccessfulFilling.observe(viewLifecycleOwner) {
            mediaPlayer = MediaPlayer.create(requireContext(), R.raw.account_fill_up)
            mediaPlayer?.start()
        }
        onFailureFilling.observe(viewLifecycleOwner) {
            mediaPlayer = MediaPlayer.create(requireContext(), R.raw.brass_fail)
            mediaPlayer?.start()
        }
        progress.observe(viewLifecycleOwner) { setHostProgress(it) }
        customer.observe(viewLifecycleOwner) { customer ->
            customer ?: return@observe
            val formatedBalance = formatPrice(customer.balance)
            binding.apply {
                title.text = customer.name
                miles.text = customer.miles
                balance.text =
                    if (customer.balance < 0.0) formatedBalance.drop(1) else formatedBalance
                balanceTitle.text = if (customer.balance < 0.0) {
                    stringsManager.getString(
                        Constants.ACCOUNT_CREDIT,
                        requireContext().getString(R.string.account_credit)
                    )
                } else {
                    stringsManager.getString(
                        Constants.ACCOUNT_BALANCE,
                        requireContext().getString(R.string.account_balance)
                    )
                }
            }
            setupCard(customer.cards)
            updateCardsInPopup(customer.cards)
        }

    }

    private fun setupLoading() = withBinding {
        title.text = "-"
        balance.text = "-"
        miles.text = "-"
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setupCard(cards: List<CreditCardModel>) {
        val firstCard = cards.maxByOrNull { it.defaultCard }
        if (firstCard != null) {
            binding.cardContainer.visibility = VISIBLE
            binding.card.text = firstCard.lastFour
            binding.defaultLabel.isVisible = firstCard.defaultCard
        } else {
            binding.cardContainer.visibility = GONE
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateCardsInPopup(cards: List<CreditCardModel>) {
        (cardAdapter as? PointerPopupAdapter)?.apply {
            val newCards = cards.mapIndexed { index, cardModel ->
                CreditCardItem(
                    id = cardModel.id,
                    default = cardModel.defaultCard,
                    lastFour = cardModel.lastFour,
                    drawBottomDivider = cards.lastIndex != index,
                    drawSelector = true
                )
            }
            clear()
            addAll(newCards)
            notifyDataSetChanged()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        popupWindow.dismiss()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}