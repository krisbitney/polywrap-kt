package io.github.krisbitney.core.resolution

import io.github.krisbitney.core.types.Client

/**
 * Defines an entity capable of resolving a wrap URI
 */
interface UriResolver {
    /**
     * Resolve a URI to a wrap package, a wrapper, or a uri
     *
     * @param uri - The URI to resolve
     * @param client - An Invoker instance that may be used to invoke a wrapper that implements the UriResolver interface
     * @param resolutionContext - The current URI resolution context
     * @return A Result containing either a wrap package, a wrapper, or a URI if successful
     */
    fun tryResolveUri(
        uri: Uri,
        client: Client,
        resolutionContext: UriResolutionContext
    ): Result<UriPackageOrWrapper>
}
