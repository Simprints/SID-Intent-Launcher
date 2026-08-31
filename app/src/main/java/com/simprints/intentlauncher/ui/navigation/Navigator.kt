package com.simprints.intentlauncher.ui.navigation

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey

/**
 * Handles navigation actions on top of the current NavigationState back stacks.
 */
class Navigator(
    private val topLevelRoutes: Set<NavKey>,
    private val getNavigationState: () -> NavigationState,
) {
    private val navigationState get() = getNavigationState()

    fun navigate(key: NavKey) {
        if (key in topLevelRoutes) {
            navigationState.topLevelRoute = key
        } else {
            currentBackStack.add(key)
        }
    }

    fun goBack(): Boolean = if (currentBackStack.size > 1) {
        currentBackStack.removeLastOrNull()
        true
    } else if (navigationState.topLevelRoute != navigationState.startRoute) {
        navigationState.topLevelRoute = navigationState.startRoute
        true
    } else {
        false
    }

    private val currentBackStack: NavBackStack<NavKey>
        get() = navigationState.backStacks[navigationState.topLevelRoute]
            ?: error("No back stack for route ${navigationState.topLevelRoute}")
}
