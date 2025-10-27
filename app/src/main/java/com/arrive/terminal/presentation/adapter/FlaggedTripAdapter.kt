package com.arrive.terminal.presentation.adapter

import com.arrive.terminal.core.ui.extensions.onClickSafe
import com.arrive.terminal.databinding.ItemFlaggedTripBinding
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding

fun flaggedTripAdapterDelegate(itemClickedListener : (FlaggedTripItem) -> Unit) = adapterDelegateViewBinding<FlaggedTripItem, RVItem, ItemFlaggedTripBinding>(
    viewBinding = { layoutInflater, root -> ItemFlaggedTripBinding.inflate(layoutInflater, root, false) }
) {
    binding.root.onClickSafe { itemClickedListener(item) }

    bind {
        with(binding) {
            name.text = item.name.asString(context)
        }
    }
}