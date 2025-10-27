package com.arrive.terminal.core.ui.navigation

import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.NavOptions
import com.arrive.terminal.R

fun Fragment.safeSetFragmentResultListener(navigation: NavigationItem) {
    if (navigation is NavigationItem.NavigateByResource) {
        setFragmentResultListener(
            requestKey = navigation.requestKey ?: return,
            listener = { requestKey, bundle ->
                navigation.resultListener?.onFragmentResult(requestKey, bundle)
            }
        )
    }
}

fun NavController.navigateByItem(navigation: NavigationItem) {
    when (navigation) {
        is NavigationItem.NavigateByResource -> {
            val navOptions = navigation.navOptions ?: run {
                NavOptions.Builder().modify(
                    useCrossFade = navigation.useCrossFade,
                    asStartDestination = navigation.asStartDestination,
                    navGraph = graph
                )
            }.build()

            navigate(navigation.id, navigation.args, navOptions)
        }
        is NavigationItem.NavigateBack -> {
            if (navigation.popupTo == null) {
                popBackStack()
            } else {
                popBackStack(navigation.popupTo, navigation.inclusive)
            }
        }
    }
}

fun NavOptions.Builder.modify(
    useCrossFade: Boolean,
    asStartDestination: Boolean,
    navGraph: NavGraph
): NavOptions.Builder {
    if (useCrossFade) applyCrossFadeAnimation()
    if (asStartDestination) clearBackStack(navGraph)
    return this
}

fun NavOptions.Builder.applyCrossFadeAnimation(): NavOptions.Builder {
    setEnterAnim(R.anim.crossfade_enter)
    setExitAnim(R.anim.corssfade_exit)
    setPopEnterAnim(R.anim.crossfade_enter)
    setPopExitAnim(R.anim.corssfade_exit)
    return this
}

private fun NavOptions.Builder.clearBackStack(graph: NavGraph): NavOptions.Builder {
    setPopUpTo(graph.startDestinationId, true)
    return this
}