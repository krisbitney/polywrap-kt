package io.github.krisbitney.uriResolvers.cache

import io.github.krisbitney.core.resolution.Uri
import io.github.krisbitney.core.types.Wrapper

/**
 * A simple cache for storing [Wrapper] instances.
 */
class BasicWrapperCache : WrapperCache {

    private val cache: MutableMap<Uri, Wrapper> = mutableMapOf()

    /**
     * Gets the [Wrapper] instance for the given [Uri].
     * @param uri The [Uri] to get the [Wrapper] for.
     * @return The [Wrapper] instance for the given [Uri], or null if it does not exist.
     */
    override fun get(uri: Uri): Wrapper? = cache[uri]

    /**
     * Sets the [Wrapper] instance for the given [Uri].
     * @param uri The [Uri] to set the [Wrapper] for.
     * @param wrapper The [Wrapper] instance to set.
     */
    override fun set(uri: Uri, wrapper: Wrapper) {
        cache[uri] = wrapper
    }
}
