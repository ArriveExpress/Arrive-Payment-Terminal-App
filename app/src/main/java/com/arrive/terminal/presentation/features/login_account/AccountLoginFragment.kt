package com.arrive.terminal.presentation.features.login_account;

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.arrive.terminal.R
import com.arrive.terminal.core.model.Constants
import com.arrive.terminal.core.ui.base.BaseVMFragment
import com.arrive.terminal.core.ui.base.Inflate
import com.arrive.terminal.core.ui.binding.bindTwoWays
import com.arrive.terminal.core.ui.extensions.disableSelectInsertText
import com.arrive.terminal.core.ui.extensions.onClickSafe
import com.arrive.terminal.databinding.FragmentAccountLoginBinding
import com.arrive.terminal.domain.manager.StringsManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AccountLoginFragment : BaseVMFragment<FragmentAccountLoginBinding, AccountLoginViewModel>() {

    override val inflate: Inflate<FragmentAccountLoginBinding> =
        FragmentAccountLoginBinding::inflate

    override val viewModel by viewModels<AccountLoginViewModel>()

    @Inject
    lateinit var stringsManager: StringsManager

    override fun FragmentAccountLoginBinding.initUI(savedInstanceState: Bundle?) {
        title.text = stringsManager.getString(
            Constants.ACCOUNT_LOGIN_TITLE,
            requireContext().getString(R.string.account_login_title)
        )
        cancel.setText(
            stringsManager.getString(
                Constants.COMMON_CANCEL,
                requireContext().getString(R.string.common_cancel)
            )
        )
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        doubleInput.inputFirst.post {
            doubleInput.inputFirst.requestFocus()
            imm.showSoftInput(doubleInput.inputFirst, InputMethodManager.SHOW_IMPLICIT)
        }
        doubleInput.apply {
            inputFirst.apply {
                setText("1")
                setSelection(text?.length ?: 0)
                hint = stringsManager.getString(
                    Constants.COMMON_HINT_ACCOUNT_NUMBER,
                    getString(R.string.common_hint_account_number)
                )
                inputType = InputType.TYPE_CLASS_NUMBER
                bindTwoWays(viewLifecycleOwner, viewModel.accountNumber)
                disableSelectInsertText()
                addTextChangedListener(object : TextWatcher {
                    var isUpdating = false

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                    override fun afterTextChanged(s: Editable?) {
                        if (isUpdating) return

                        isUpdating = true

                        val original = s.toString()
                        val filtered = original.filter { it.isDigit() }

                        val newText = if (filtered.startsWith("1")) {
                            filtered.take(11)
                        } else {
                            "1" + filtered.take(10)
                        }

                        if (newText != original) {
                            setText(newText)
                            setSelection(newText.length)
                        }

                        isUpdating = false
                    }
                })
            }
            inputSeconds.apply {
                hint = stringsManager.getString(
                    Constants.COMMON_HINT_PIN,
                    getString(R.string.common_hint_pin)
                )
                inputType = InputType.TYPE_CLASS_NUMBER
                bindTwoWays(viewLifecycleOwner, viewModel.pin)
                disableSelectInsertText()
            }
            continueAction.isVisible = true
            continueAction.isClickable = true

//            //todo: delete
//            inputFirst.setText("18452639769")
//            inputSeconds.setText("2839")
        }

        // listener
        cancel.onClickSafe { viewModel.navigateBack() }
        doubleInput.continueAction.onClickSafe {
            viewModel.onLoginClick()
        }
    }

    override fun AccountLoginViewModel.observeViewModel() {
        isActionButtonClickable.observe(viewLifecycleOwner) {
            binding.doubleInput.continueAction.isClickable = it
        }
        loginProgress.observe(viewLifecycleOwner) {
            setHostProgress(it)
        }
    }
}