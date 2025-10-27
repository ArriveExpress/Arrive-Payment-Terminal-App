package com.arrive.terminal.core.ui.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding

typealias SimpleClickListener = () -> Unit

typealias Inflate<T> = (LayoutInflater, ViewGroup?, Boolean) -> T

typealias LayoutInflate<T> = (LayoutInflater) -> T

class ViewBindingHolder<T : ViewBinding> {

    private var _binding: T? = null
    val binding: T get() = _binding!!

    fun createBinding(lifecycleOwner: LifecycleOwner, creator: () -> T): T {
        val newBinding = creator()
        _binding = newBinding

        lifecycleOwner.lifecycle.addObserver(
            object : DefaultLifecycleObserver {

                override fun onDestroy(owner: LifecycleOwner) {
                    _binding = null
                    lifecycleOwner.lifecycle.removeObserver(this)
                }
            }
        )

        return newBinding
    }
}