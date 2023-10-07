@file:Suppress("INLINE_FROM_HIGHER_PLATFORM")

package com.rdev.tt._utils

import androidx.compose.runtime.Composable
import dev.icerock.moko.mvvm.compose.ViewModelFactory
import dev.icerock.moko.mvvm.compose.getViewModel
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import org.koin.mp.KoinPlatformTools
import kotlin.reflect.KClass

@Composable
fun <VM : ViewModel> koinViewModel(clazz: KClass<VM>): VM {
    val factoryBinding = KoinPlatformTools.defaultContext().get().get<ViewModelFactoryBinding>()

    val factory = factoryBinding.get(clazz)
        ?: throw IllegalStateException("${clazz.simpleName} has not been registered to Koin.")

    return getViewModel(
        key = clazz.qualifiedName.orEmpty(),
        factory = factory
    )
}

@Suppress("UNCHECKED_CAST")
class ViewModelFactoryBinding(vararg factories: Pair<String, () -> ViewModel>) {
    val bindings: Map<String, () -> ViewModel> = factories.toMap()

    fun <T : ViewModel> get(clazz: KClass<T>): ViewModelFactory<T>? {
        val key = clazz.qualifiedName ?: return null
        if (key !in bindings) return null

        return object : ViewModelFactory<T> {
            override val kClass: KClass<T>
                get() = clazz

            override fun createViewModel(): T {
                return bindings[key]!!.invoke() as T
            }
        }
    }
}