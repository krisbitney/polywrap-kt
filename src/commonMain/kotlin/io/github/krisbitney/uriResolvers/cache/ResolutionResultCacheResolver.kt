package io.github.krisbitney.uriResolvers.cache

import io.github.krisbitney.core.resolution.*
import io.github.krisbitney.core.types.*

/**
 * A URI resolver that uses a [ResolutionResultCache] to store and retrieve the results of resolved URIs.
 *
 * @property resolver The [UriResolver] to use when resolving URIs.
 * @property cache The cache to store and retrieve resolved URIs.
 */
class ResolutionResultCacheResolver(
    private val resolver: UriResolver,
    private val cache: ResolutionResultCache
) : UriResolver {

    /**
     * Tries to resolve the given URI using a cache and returns the result.
     *
     * @param uri The URI to resolve.
     * @param client The invoker of the resolution.
     * @param resolutionContext The context for the resolution.
     * @return A [Result] containing the resolved [UriPackageOrWrapper] on success, or an exception on failure.
     */
    override fun tryResolveUri(
        uri: Uri,
        client: Client,
        resolutionContext: UriResolutionContext,
    ): Result<UriPackageOrWrapper> {
        val uriPackageOrWrapper = cache.get(uri)

        // return from cache if available
        if (uriPackageOrWrapper != null) {
            val result = Result.success(uriPackageOrWrapper)
            resolutionContext.trackStep(
                UriResolutionStep(
                    sourceUri = uri,
                    result = result,
                    description = "ResolutionResultCacheResolver (Cache)"
                )
            )
            return result
        }

        // resolve uri if not in cache
        val subContext = resolutionContext.createSubHistoryContext()
        val result = resolver.tryResolveUri(uri, client, subContext).onSuccess {
            this.cache.set(uri, it)
        }

        resolutionContext.trackStep(
            UriResolutionStep(
                sourceUri = uri,
                result = result,
                subHistory = subContext.getHistory(),
                description = "ResolutionResultCacheResolver"
            )
        )

        return result
    }
}
