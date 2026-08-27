package com.simprints.intentlauncher.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

interface UiState

interface UiAction

interface UiEffect

abstract class BaseMviViewModel<S : UiState, A : UiAction, E : UiEffect>(
    initialState: S,
) : ViewModel() {
    // Protected contract for derived classes to override if necessary
    protected open val internalState = MutableStateFlow(initialState)
    protected open val internalEffects = MutableSharedFlow<E>(replay = 0, extraBufferCapacity = 1)

    protected abstract suspend fun handleAction(action: A)

    // Public contract for consumer classes
    val state: StateFlow<S> = internalState
    val effects: Flow<E> = internalEffects

    fun onAction(action: A) = viewModelScope.launch { handleAction(action) }

    // Non-overrideable internals

    protected fun reduce(reducer: (S) -> S) {
        internalState.update(reducer)
    }

    protected fun emitEffect(effect: E) {
        internalEffects.tryEmit(effect)
    }
}
