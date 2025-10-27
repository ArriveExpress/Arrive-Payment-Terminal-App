package com.arrive.terminal.presentation.features.login_account;

import android.os.Bundle
import android.text.InputType
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.arrive.terminal.R
import com.arrive.terminal.core.ui.base.BaseVMFragment
import com.arrive.terminal.core.ui.base.Inflate
import com.arrive.terminal.core.ui.binding.bindTwoWays
import com.arrive.terminal.core.ui.extensions.disableSelectInsertText
import com.arrive.terminal.core.ui.extensions.onClickSafe
import com.arrive.terminal.databinding.FragmentAccountLoginBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccountLoginFragment : BaseVMFragment<FragmentAccountLoginBinding, AccountLoginViewModel>() {

    override val inflate: Inflate<FragmentAccountLoginBinding> =
        FragmentAccountLoginBinding::inflate

    override val viewModel by viewModels<AccountLoginViewModel>()

    override fun FragmentAccountLoginBinding.initUI(savedInstanceState: Bundle?) {
        doubleInput.apply {
            inputFirst.apply {
                hint = getString(R.string.common_hint_account_number)
                inputType = InputType.TYPE_CLASS_NUMBER
                bindTwoWays(viewLifecycleOwner, viewModel.accountNumber)
                disableSelectInsertText()
            }
            inputSeconds.apply {
                hint = getString(R.string.common_hint_pin)
                inputType = InputType.TYPE_CLASS_NUMBER
                bindTwoWays(viewLifecycleOwner, viewModel.pin)
                disableSelectInsertText()
            }
            continueAction.isVisible = true

//            //todo: delete
//            inputFirst.setText("18452639769")
//            inputSeconds.setText("2839")
        }

        // listener
        cancel.onClickSafe { viewModel.navigateBack() }
        doubleInput.continueAction.onClickSafe { viewModel.onLoginClick() }
    }

    override fun AccountLoginViewModel.observeViewModel() {
        loginProgress.observe(viewLifecycleOwner) { setHostProgress(it) }
    }
}