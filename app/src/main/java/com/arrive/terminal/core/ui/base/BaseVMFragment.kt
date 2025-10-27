package com.arrive.terminal.core.ui.base;

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.arrive.terminal.R
import com.arrive.terminal.core.ui.extensions.applyInsets
import com.arrive.terminal.core.ui.extensions.clearFocusHideKeyboard
import com.arrive.terminal.core.ui.extensions.isValid
import com.arrive.terminal.core.ui.extensions.restartApplication
import com.arrive.terminal.core.ui.extensions.toast
import com.arrive.terminal.core.ui.model.StringValue
import com.arrive.terminal.core.ui.navigation.NavigationItem
import com.arrive.terminal.core.ui.navigation.navigateByItem
import com.arrive.terminal.core.ui.navigation.safeSetFragmentResultListener
import com.arrive.terminal.core.ui.utils.STUB
import com.arrive.terminal.core.ui.utils.safe
import com.arrive.terminal.presentation.host.MainHost

abstract class BaseVMFragment<VB : ViewBinding, VM : BaseViewModel> : Fragment() {

    protected lateinit var mainHandler: Handler

    protected abstract val inflate: Inflate<VB>
    protected abstract val viewModel: VM

    private var _binding: VB? = null
    protected val binding get() = _binding!!

    protected val mainHost: MainHost?
        get() = activity as? MainHost

    protected val applyInsetsTo: View get() = binding.root

    open fun VB.initUI(savedInstanceState: Bundle?) = STUB

    open fun VM.observeViewModel() = STUB

    open fun handleBackPressed() {
        if (findNavController().popBackStack().not()) {
            requireActivity().finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainHandler = Handler(Looper.getMainLooper())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return inflate.invoke(inflater, container, false).also {
            _binding = it
            it.initUI(savedInstanceState)
            applyInsetsTo.applyInsets()
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.observeViewModel()
        handleBaseViewModel(viewModel)
        viewModel.onViewLoaded()
    }

    protected fun setTitle(title: StringValue?) {
        (activity as? MainHost)?.onChangeTitle(title)
    }

    protected fun setHostProgress(visible: Boolean) {
        (activity as? MainHost)?.onShowHostProgress(visible)
    }

    protected fun withBinding(block: VB.() -> Unit) {
        _binding
            .takeIf { context.isValid }
            ?.let { block(it) }
    }

    private fun handleBaseViewModel(viewModel: BaseViewModel) = with(viewModel) {
        onShowToast.observe(viewLifecycleOwner) { requireContext().toast(it.asString(requireContext())) }
        onNavigate.observe(viewLifecycleOwner, ::handleNavigation)
        onClearFocusHideKeyboard.observe(viewLifecycleOwner) { clearFocusHideKeyboard(binding.root) }
        onRestartApplication.observe(viewLifecycleOwner) { context?.restartApplication() }
    }

    protected fun handleNavigation(navigation: NavigationItem) {
        try {
            findNavController().navigateByItem(navigation)
            safeSetFragmentResultListener(navigation)
            return
        } catch (exception: Exception) {
            var requiredParentFragment: Fragment? = parentFragment
            while (requiredParentFragment != null) {
                requiredParentFragment = try {
                    val navController = requiredParentFragment.findNavController()
                    navController.navigateByItem(navigation)
                    requiredParentFragment.safeSetFragmentResultListener(navigation)
                    return
                } catch (exception: Exception) {
                    requiredParentFragment.parentFragment
                }
            }
        }
        navigateByActivity(navigation)
    }

    private fun navigateByActivity(item: NavigationItem) {
        safe {
            requireActivity()
                .findNavController(R.id.navHostFragment)
                .navigateByItem(item)
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}