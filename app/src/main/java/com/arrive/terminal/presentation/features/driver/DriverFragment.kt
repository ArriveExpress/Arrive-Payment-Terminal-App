package com.arrive.terminal.presentation.features.driver;

import android.os.Bundle
import androidx.fragment.app.viewModels
import com.arrive.terminal.core.ui.base.BaseVMFragment
import com.arrive.terminal.core.ui.base.Inflate
import com.arrive.terminal.core.ui.extensions.onClickSafe
import com.arrive.terminal.core.ui.extensions.underline
import com.arrive.terminal.core.ui.helper.TimeUpdater
import com.arrive.terminal.databinding.FragmentDriverBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DriverFragment : BaseVMFragment<FragmentDriverBinding, DriverViewModel>() {

    override val inflate: Inflate<FragmentDriverBinding> = FragmentDriverBinding::inflate

    override val viewModel by viewModels<DriverViewModel>()

    override fun FragmentDriverBinding.initUI(savedInstanceState: Bundle?) {
        mainHost?.apply {
            subscribePaymentEvents()
        }
        lifecycle.addObserver(TimeUpdater(binding.date))

        // listeners
        myAccount.onClickSafe { viewModel.onMyAccountClick() }
    }

    override fun DriverViewModel.observeViewModel() {
        driverId.observe(viewLifecycleOwner) {
            binding.driverId.text = it.asString(requireContext())
            binding.driverId.underline()
        }
    }
}