package io.github.krisbitney.uriResolvers.cache

import io.github.krisbitney.core.resolution.Uri
import io.github.krisbitney.core.resolution.UriPackageOrWrapper

/**
 * A simple cache for storing [UriPackageOrWrapper] instances.
 */
class BasicResolutionResultCache : ResolutionResultCache {

    private val cache: MutableMap<Uri, UriPackageOrWrapper> = mutableMapOf()

    override fun get(uri: Uri): UriPackageOrWrapper? = cache[uri]

    override fun set(uri: Uri, value: UriPackageOrWrapper) {
        cache[uri] = value
    }
}
