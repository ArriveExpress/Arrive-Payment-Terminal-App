package com.arrive.terminal.presentation.features.account;

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.viewModels
import com.arrive.terminal.core.ui.base.BaseVMFragment
import com.arrive.terminal.core.ui.base.Inflate
import com.arrive.terminal.core.ui.extensions.disableSelectInsertText
import com.arrive.terminal.core.ui.extensions.onClickSafe
import com.arrive.terminal.core.ui.extensions.underline
import com.arrive.terminal.core.ui.helper.KeyboardHelper.clearFocusHideKeyboard
import com.arrive.terminal.core.ui.helper.KeyboardHelper.showKeyboard
import com.arrive.terminal.core.ui.utils.formatPrice
import com.arrive.terminal.databinding.FragmentAccountBinding
import com.arrive.terminal.domain.model.CreditCardModel
import com.arrive.terminal.presentation.adapter.CreditCardItem
import com.arrive.terminal.presentation.adapter.creditCardAdapterDelegate
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccountFragment : BaseVMFragment<FragmentAccountBinding, AccountViewModel>() {

    override val inflate: Inflate<FragmentAccountBinding> = FragmentAccountBinding::inflate

    override val viewModel by viewModels<AccountViewModel>()

    private val cardsAdapter = ListDelegationAdapter(
        creditCardAdapterDelegate {
            viewModel.onCardSelected(it)
        }
    )

    override fun FragmentAccountBinding.initUI(savedInstanceState: Bundle?) {
        setupLoading()
        addNew.underline()
        cardsList.adapter = cardsAdapter

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
            variantOther.setText("Other")
            activity?.window?.clearFocusHideKeyboard(binding.root)
        }

        variant50.isSelected = true
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
                variantOther.setText("Other")
            }
        }

        viewModel.loadCustomer()
    }

    override fun AccountViewModel.observeViewModel() {
        progress.observe(viewLifecycleOwner) { setHostProgress(it) }
        customer.observe(viewLifecycleOwner) { customer ->
            customer ?: return@observe
            binding.apply {
                title.text = customer.name
                miles.text = customer.miles
                balance.text = formatPrice(customer.balance)
                setupCards(customer.cards)
            }
        }
    }

    private fun setupLoading() = withBinding {
        title.text = "-"
        balance.text = "-"
        miles.text = "-"
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setupCards(cards: List<CreditCardModel>) {
        cardsAdapter.items = cards.mapIndexed { index, cardModel ->
            CreditCardItem(
                id = cardModel.id,
                default = cardModel.defaultCard,
                lastFour = cardModel.lastFour,
                drawBottomDivider = cards.lastIndex != index,
                drawSelector = true
            )
        }
        cardsAdapter.notifyDataSetChanged()
    }
}