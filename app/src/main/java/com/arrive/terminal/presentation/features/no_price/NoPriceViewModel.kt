package com.arrive.terminal.presentation.features.no_price;

import com.arrive.terminal.core.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NoPriceViewModel @Inject constructor() : BaseViewModel() {

    fun onDoneClick() {
        navigateBack()
    }
}