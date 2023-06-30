package io.github.krisbitney.uriResolvers.cache

import io.github.krisbitney.core.resolution.Uri
import io.github.krisbitney.core.resolution.UriPackageOrWrapper

/**
 * A cache for storing [UriPackageOrWrapper] instances.
 */
interface ResolutionResultCache {
    /**
     * Gets the [UriPackageOrWrapper] instance for the given [Uri].
     * @param uri The [Uri] to get the [UriPackageOrWrapper] for.
     * @return The [UriPackageOrWrapper] instance for the given [Uri], or null if it does not exist.
     */
    fun get(uri: Uri): UriPackageOrWrapper?

    /**
     * Sets the [UriPackageOrWrapper] instance for the given [Uri].
     * @param uri The [Uri] to set the [UriPackageOrWrapper] for.
     * @param value The [UriPackageOrWrapper] instance to set.
     */
    fun set(uri: Uri, value: UriPackageOrWrapper)
}
