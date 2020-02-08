package com.binarysushi.studio

import com.intellij.DynamicBundle
import org.jetbrains.annotations.NonNls
import org.jetbrains.annotations.PropertyKey

@NonNls
private const val BUNDLE = "messages.StudioBundle"

object StudioBundle : DynamicBundle(BUNDLE) {
    @JvmStatic
    fun message(@PropertyKey(resourceBundle = BUNDLE) key: String, vararg params: Any): String = getMessage(key, *params)

    @JvmStatic
    fun lazyMessage(@PropertyKey(resourceBundle = BUNDLE) key: String,
                    vararg params: Any): java.util.function.Supplier<String> = getLazyMessage(key, *params)
}
