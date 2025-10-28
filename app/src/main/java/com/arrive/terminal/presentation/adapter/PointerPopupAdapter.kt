package com.arrive.terminal.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import com.arrive.terminal.core.ui.extensions.onClickSafe
import com.arrive.terminal.databinding.ItemCardBinding

class PointerPopupAdapter(
    context: Context,
    private val usersModels: List<CreditCardItem>,
    private val onClick: (card: CreditCardItem) -> Unit
) : ArrayAdapter<CreditCardItem>(context, 0, usersModels) {

    override fun getCount(): Int = usersModels.size

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding = ItemCardBinding.inflate(LayoutInflater.from(context), parent, false)
        val holder = PopupTitleViewHolder(binding)
        holder.bind(usersModels[position])
        binding.root.onClickSafe { onClick(usersModels[position]) }
        return binding.root
    }

    inner class PopupTitleViewHolder(private val binding: ItemCardBinding) {
        fun bind(card: CreditCardItem) {
            with(binding) {
                this.card.text = card.lastFour
                defaultLabel.isVisible = card.default
                selector.isVisible = card.drawSelector
                selector.isSelected = card.default
            }
        }
    }
}