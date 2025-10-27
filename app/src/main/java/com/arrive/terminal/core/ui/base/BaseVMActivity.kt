package com.arrive.terminal.core.ui.base;

import android.os.Bundle
import androidx.navigation.NavController
import androidx.viewbinding.ViewBinding
import com.arrive.terminal.core.ui.extensions.toast
import com.arrive.terminal.core.ui.navigation.navigateByItem
import com.arrive.terminal.core.ui.utils.STUB

abstract class BaseVMActivity<VB : ViewBinding, VM : BaseViewModel> : BaseBindingActivity<VB>() {

    protected abstract val viewModel: VM

    open val navController: NavController? = null

    /**
     * Override in subclasses if needed
     */
    open fun VM.observeViewModel() = STUB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.observeViewModel()
        handleBaseViewModel(viewModel)
        viewModel.onViewLoaded()
    }

    private fun handleBaseViewModel(viewModel: BaseViewModel) = with(viewModel) {
        onShowToast.observe(this@BaseVMActivity) {
            toast(it.asString(this@BaseVMActivity))
        }
        onNavigate.observe(this@BaseVMActivity) {
            navController?.navigateByItem(it)
        }
    }
}