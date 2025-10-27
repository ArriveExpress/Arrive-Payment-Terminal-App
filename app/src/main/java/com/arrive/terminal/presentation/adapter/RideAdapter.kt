package com.arrive.terminal.presentation.adapter

import com.arrive.terminal.core.ui.extensions.drawableOrGone
import com.arrive.terminal.core.ui.extensions.getDrawableCompat
import com.arrive.terminal.core.ui.extensions.onClickSafe
import com.arrive.terminal.core.ui.extensions.textOrGoneIfBlank
import com.arrive.terminal.databinding.ItemRideBinding
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding

fun rideAdapterDelegate(itemClickedListener : (RideItem) -> Unit) = adapterDelegateViewBinding<RideItem, RVItem, ItemRideBinding>(
    viewBinding = { layoutInflater, root -> ItemRideBinding.inflate(layoutInflater, root, false) }
) {
    binding.root.onClickSafe { itemClickedListener(item) }

    bind {
        with(binding) {
            icon.drawableOrGone(item.icon?.let { context.getDrawableCompat(it) })
            name.text = item.name.asString(context)
            price.textOrGoneIfBlank(item.price)
            icon.drawableOrGone(item.icon?.let { context.getDrawableCompat(it) })
        }
    }
}