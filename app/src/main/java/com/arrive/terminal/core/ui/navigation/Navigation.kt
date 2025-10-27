package com.arrive.terminal.core.ui.navigation

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.fragment.app.FragmentResultListener
import androidx.navigation.NavOptions

sealed interface NavigationItem {

    /**
     * Class for navigation by Navigation Components
     * @param navOptions - if you are using this parameter, [asStartDestination], [useCrossFade] and
     * xml action navigation parameter will be ignored
     */
    data class NavigateByResource(
        @IdRes val id: Int,
        val args: Bundle? = null,
        val navOptions: NavOptions? = null,
        val asStartDestination: Boolean = false,
        val useCrossFade: Boolean = false,
        val requestKey: String? = null,
        val resultListener: FragmentResultListener? = null
    ) : NavigationItem

    data class NavigateBack(
        @IdRes val popupTo: Int? = null,
        val inclusive: Boolean = false
    ) : NavigationItem
}