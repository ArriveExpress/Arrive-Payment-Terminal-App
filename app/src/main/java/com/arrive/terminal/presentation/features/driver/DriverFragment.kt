package com.arrive.terminal.presentation.features.driver;

import android.os.Bundle
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.arrive.terminal.R
import com.arrive.terminal.core.model.Constants
import com.arrive.terminal.core.ui.base.BaseVMFragment
import com.arrive.terminal.core.ui.base.Inflate
import com.arrive.terminal.core.ui.extensions.onClickSafe
import com.arrive.terminal.core.ui.extensions.underline
import com.arrive.terminal.core.ui.helper.TimeUpdater
import com.arrive.terminal.databinding.FragmentDriverBinding
import com.arrive.terminal.domain.manager.StringsManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DriverFragment : BaseVMFragment<FragmentDriverBinding, DriverViewModel>() {

    override val inflate: Inflate<FragmentDriverBinding> = FragmentDriverBinding::inflate

    @Inject
    lateinit var stringsManager: StringsManager

    override val viewModel by viewModels<DriverViewModel>()

    override fun FragmentDriverBinding.initUI(savedInstanceState: Bundle?) {
        title.text = stringsManager.getString(
            Constants.DRIVER_TITLE,
            requireContext().getString(R.string.driver_title)
        )
        myAccount.setText(
            stringsManager.getString(
                Constants.DRIVER_MY_ACCOUNT,
                requireContext().getString(R.string.driver_my_account)
            )
        )
        mainHost?.apply {
            subscribePaymentEvents()
            getWeatherUpdateEvent().observe(viewLifecycleOwner) {
                viewModel.refreshWeather()
            }
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

        weather.observe(viewLifecycleOwner) { weatherModel ->
            binding.weatherWidget.isVisible = weatherModel != null
            weatherModel?.let {
                binding.weatherWidget.updateWeather(it)
            }
        }
    }
}