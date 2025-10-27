package com.arrive.terminal.presentation.features.no_price;

import android.os.Bundle
import androidx.fragment.app.viewModels
import com.arrive.terminal.core.ui.base.BaseVMFragment
import com.arrive.terminal.core.ui.base.Inflate
import com.arrive.terminal.core.ui.extensions.onClickSafe
import com.arrive.terminal.databinding.FragmentNoPriceBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NoPriceFragment : BaseVMFragment<FragmentNoPriceBinding, NoPriceViewModel>() {

    override val inflate: Inflate<FragmentNoPriceBinding> = FragmentNoPriceBinding::inflate

    override val viewModel by viewModels<NoPriceViewModel>()

    override fun FragmentNoPriceBinding.initUI(savedInstanceState: Bundle?) {
        setTitle(title = null)
        action.onClickSafe { viewModel.onDoneClick() }
    }
}