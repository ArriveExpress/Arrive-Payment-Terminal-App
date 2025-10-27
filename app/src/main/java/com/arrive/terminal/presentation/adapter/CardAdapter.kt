package com.arrive.terminal.presentation.adapter;

import androidx.core.view.isVisible
import com.arrive.terminal.core.ui.extensions.onClickSafe
import com.arrive.terminal.databinding.ItemCardBinding
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding

fun creditCardAdapterDelegate(itemClickedListener: (CreditCardItem) -> Unit) =
    adapterDelegateViewBinding<CreditCardItem, RVItem, ItemCardBinding>(
        viewBinding = { layoutInflater, root ->
            ItemCardBinding.inflate(
                layoutInflater,
                root,
                false
            )
        }
    ) {
        binding.apply {
            root.onClickSafe { itemClickedListener(item) }
        }

        bind {
            with(binding) {
                selector.isVisible = item.drawSelector
                selector.isSelected = item.default
                card.text = item.lastFour
                defaultLabel.isVisible = item.default
                divider.isVisible = item.drawBottomDivider
            }
        }
    }