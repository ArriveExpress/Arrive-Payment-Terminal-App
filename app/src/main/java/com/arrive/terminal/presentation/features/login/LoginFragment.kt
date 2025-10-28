package com.arrive.terminal.presentation.features.login;

import android.os.Bundle
import android.text.InputType
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.viewModels
import com.arrive.terminal.R
import com.arrive.terminal.core.model.Constants
import com.arrive.terminal.core.ui.base.BaseVMFragment
import com.arrive.terminal.core.ui.base.Inflate
import com.arrive.terminal.core.ui.binding.bindTwoWays
import com.arrive.terminal.core.ui.extensions.disableSelectInsertText
import com.arrive.terminal.core.ui.extensions.onClickSafe
import com.arrive.terminal.core.ui.model.StringValue.Companion.asStringValue
import com.arrive.terminal.databinding.FragmentLoginBinding
import com.arrive.terminal.domain.manager.StringsManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : BaseVMFragment<FragmentLoginBinding, LoginViewModel>() {

    override val inflate: Inflate<FragmentLoginBinding> = FragmentLoginBinding::inflate

    override val viewModel by viewModels<LoginViewModel>()

    @Inject
    lateinit var stringsManager: StringsManager

    override fun FragmentLoginBinding.initUI(savedInstanceState: Bundle?) {
        setTitle(
            stringsManager.getString(
                Constants.LOGIN_DRIVER_TITLE,
                requireContext().getString(R.string.login_driver_title)
            ).asStringValue
        )
        login.setText(
            stringsManager.getString(
                Constants.LOGIN_TO_DRIVER_ACTION,
                requireContext().getString(R.string.login_to_driver_action)
            )
        )
        input.editText.hint = stringsManager.getString(
            Constants.LOGIN_ENTER_DRIVER_ID,
            requireContext().getString(R.string.login_enter_driver_id)
        )
        input.editText.bindTwoWays(viewLifecycleOwner, viewModel.driverId)
        input.editText.inputType = InputType.TYPE_CLASS_NUMBER
        input.editText.imeOptions = EditorInfo.IME_ACTION_DONE
        input.editText.disableSelectInsertText()
        login.onClickSafe { viewModel.onLoginClick() }

        //todo: delete
//        input.editText.setText("1000")
    }

    override fun LoginViewModel.observeViewModel() {
        loginProgress.observe(viewLifecycleOwner) { binding.login.setProgress(it) }
    }
}