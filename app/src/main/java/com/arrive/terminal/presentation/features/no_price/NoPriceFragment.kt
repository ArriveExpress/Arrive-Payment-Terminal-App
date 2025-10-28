package com.arrive.terminal.presentation.features.no_price;

import android.os.Bundle
import androidx.fragment.app.viewModels
import com.arrive.terminal.R
import com.arrive.terminal.core.model.Constants
import com.arrive.terminal.core.ui.base.BaseVMFragment
import com.arrive.terminal.core.ui.base.Inflate
import com.arrive.terminal.core.ui.extensions.onClickSafe
import com.arrive.terminal.databinding.FragmentNoPriceBinding
import com.arrive.terminal.domain.manager.StringsManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NoPriceFragment : BaseVMFragment<FragmentNoPriceBinding, NoPriceViewModel>() {

    override val inflate: Inflate<FragmentNoPriceBinding> = FragmentNoPriceBinding::inflate

    override val viewModel by viewModels<NoPriceViewModel>()

    @Inject
    lateinit var stringsManager: StringsManager

    override fun FragmentNoPriceBinding.initUI(savedInstanceState: Bundle?) {
        setTitle(title = null)
        title.text = stringsManager.getString(
            Constants.NO_PRICE_SET_TITLE,
            requireContext().getString(R.string.no_price_set_title)
        )
        message.text = stringsManager.getString(
            Constants.NO_RICE_MESSAGE,
            requireContext().getString(R.string.no_rice_message)
        )
        action.setText(
            stringsManager.getString(
                Constants.NO_PRICE_ACTION,
                requireContext().getString(R.string.no_price_action)
            )
        )
        action.onClickSafe { viewModel.onDoneClick() }
    }
}