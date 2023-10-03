@file:Suppress("INLINE_FROM_HIGHER_PLATFORM")

package com.rdev.tt._utils

import androidx.compose.runtime.Composable
import dev.icerock.moko.mvvm.compose.ViewModelFactory
import dev.icerock.moko.mvvm.compose.getViewModel
import dev.icerock.moko.mvvm.compose.viewModelFactory
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import org.koin.mp.KoinPlatformTools

@Composable
inline fun <reified VM : ViewModel> koinViewModel(): VM {
    val factoryBinding = KoinPlatformTools.defaultContext().get().get<ViewModelFactoryBinding>()
    val factory = factoryBinding.get<VM>()
        ?: throw IllegalStateException("${VM::class.simpleName} has not been registered to Koin.")

    return getViewModel(
        key = VM::class.qualifiedName.orEmpty(),
        factory = factory
    )
}

class ViewModelFactoryBinding(vararg factories: Pair<String, () -> ViewModel>) {
    val bindings: Map<String, () -> ViewModel> = factories.toMap()

    inline fun <reified T : ViewModel> get(): ViewModelFactory<T>? {
        val key = T::class.qualifiedName ?: return null
        if (key !in bindings) return null

        return viewModelFactory { bindings[key]!!.invoke() as T }
    }
}