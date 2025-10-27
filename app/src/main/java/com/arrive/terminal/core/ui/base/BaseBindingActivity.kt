package com.arrive.terminal.core.ui.base;

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.viewbinding.ViewBinding
import com.arrive.terminal.core.ui.utils.STUB

abstract class BaseBindingActivity<VB : ViewBinding> : AppCompatActivity() {

    protected abstract val inflater: LayoutInflate<VB>

    private val bindingHolder = ViewBindingHolder<VB>()

    protected val binding get() = bindingHolder.binding

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        bindingHolder.createBinding(this) {
            inflater(layoutInflater)
        }
        setContentView(binding.root)
        binding.initUI(savedInstanceState)
    }

    /**
     * Initializes the user interface elements.
     * This method is called after the view is inflated and before it is displayed to the user.
     * @param savedInstanceState If non-null, this activity is being re-constructed from a previous saved state.
     * @see onCreate(Bundle)
     */
    open fun VB.initUI(savedInstanceState: Bundle?) = STUB

    protected open fun onViewModelOnLoad() = STUB
}