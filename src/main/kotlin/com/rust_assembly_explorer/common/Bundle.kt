package com.rust_assembly_explorer.common

import com.intellij.DynamicBundle
import org.jetbrains.annotations.Nls
import org.jetbrains.annotations.PropertyKey

private const val BUNDLE = "messages.RustAssemblyExplorer"

object Bundle : DynamicBundle(BUNDLE) {
    @Nls
    fun message(
        @PropertyKey(resourceBundle = BUNDLE) key: String,
        vararg params: Any
    ): String {
        return getMessage(key, *params)
    }
}
