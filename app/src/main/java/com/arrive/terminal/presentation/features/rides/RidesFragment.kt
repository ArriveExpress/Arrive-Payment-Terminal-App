package com.arrive.terminal.presentation.features.rides;

import android.os.Bundle
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.arrive.terminal.R
import com.arrive.terminal.core.model.Constants
import com.arrive.terminal.core.model.isErrorState
import com.arrive.terminal.core.model.isLoadingState
import com.arrive.terminal.core.model.isSuccessState
import com.arrive.terminal.core.model.onError
import com.arrive.terminal.core.model.onSuccess
import com.arrive.terminal.core.ui.base.BaseVMFragment
import com.arrive.terminal.core.ui.base.Inflate
import com.arrive.terminal.core.ui.extensions.dpToPx
import com.arrive.terminal.core.ui.extensions.onClickSafe
import com.arrive.terminal.core.ui.model.StringValue.Companion.asStringValue
import com.arrive.terminal.core.ui.view.recyclerview.decorator.SpaceConfig
import com.arrive.terminal.core.ui.view.recyclerview.decorator.SpaceDividerDecorator
import com.arrive.terminal.databinding.FragmentRidesBinding
import com.arrive.terminal.domain.manager.StringsManager
import com.arrive.terminal.presentation.adapter.flaggedTripAdapterDelegate
import com.arrive.terminal.presentation.adapter.rideAdapterDelegate
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RidesFragment : BaseVMFragment<FragmentRidesBinding, RidesViewModel>() {

    override val inflate: Inflate<FragmentRidesBinding> = FragmentRidesBinding::inflate

    override val viewModel by viewModels<RidesViewModel>()

    private val adapter = ListDelegationAdapter(
        rideAdapterDelegate { viewModel.onRideClick(it) },
        flaggedTripAdapterDelegate { viewModel.onFlaggedTripClick(it) }
    )

    @Inject
    lateinit var stringsManager: StringsManager

    override fun FragmentRidesBinding.initUI(savedInstanceState: Bundle?) {
        setTitle(
            stringsManager.getString(
                Constants.RIDES_CONFIRM_YOUR_RIDE,
                requireContext().getString(R.string.rides_confirm_your_ride)
            ).asStringValue
        )
        rides.addItemDecoration(SpaceDividerDecorator(onDrawSpace = { _, _, _ ->
            SpaceConfig(vertical = requireContext().dpToPx(4), horizontal = 0)
        }))
        rides.adapter = adapter
        stateContainer.stateAction.onClickSafe { viewModel.loadRides(force = true) }
    }

    override fun RidesViewModel.observeViewModel() = withBinding {
        ridesState.observe(viewLifecycleOwner) { state ->
            rides.isVisible = state.isSuccessState
            stateContainer.root.isVisible = state.isErrorState || state.isLoadingState
            stateContainer.stateProgress.isVisible = state.isLoadingState
            stateContainer.stateErrorGroup.isVisible = state.isErrorState

            state
                .onSuccess { adapter.items = it }
                .onError { stateContainer.stateMessage.text = it.message?.asString(requireContext()) }
        }
    }
}