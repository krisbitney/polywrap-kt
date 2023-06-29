package io.github.krisbitney.uriResolvers.embedded

import io.github.krisbitney.core.resolution.Uri
import io.github.krisbitney.core.resolution.UriPackageOrWrapper
import io.github.krisbitney.core.resolution.UriResolutionContext
import io.github.krisbitney.core.resolution.UriResolver
import io.github.krisbitney.core.types.Client
import io.github.krisbitney.uriResolvers.ResolverWithHistory

/**
 * A concrete implementation of [ResolverWithHistory] that redirects a URI to another URI.
 *
 * @property from The URI to redirect from.
 * @property to The URI to redirect to.
 */
class UriRedirectResolver(val from: Uri, val to: Uri) : ResolverWithHistory() {

    /**
     * Returns a human-readable description of the resolution step for this [UriRedirectResolver].
     *
     * @param uri The URI being resolved.
     * @param result The [Result] containing a wrap package, a wrapper, or a URI if successful.
     * @return A [String] description of the resolution step.
     */
    override fun getStepDescription(uri: Uri, result: Result<UriPackageOrWrapper>): String =
        "Redirect (${this.from.uri} - ${this.to.uri})"

    /**
     * Tries to resolve the given [uri] by redirecting it to another URI.
     *
     * @param uri The URI to resolve.
     * @param client The [Client] instance used to invoke a wrapper implementing the [UriResolver] interface.
     * @param resolutionContext The current URI resolution context.
     * @param resolveToPackage Whether to resolve the URI to a wrap package (ignored by this resolver).
     * @return A [Result] containing a wrap package, a wrapper, or a URI if successful.
     */
    override fun _tryResolveUri(
        uri: Uri,
        client: Client,
        resolutionContext: UriResolutionContext,
        resolveToPackage: Boolean
    ): Result<UriPackageOrWrapper> {
        return if (uri.uri != this.from.uri) {
            Result.success(UriPackageOrWrapper.UriValue(uri))
        } else {
            Result.success(UriPackageOrWrapper.UriValue(to))
        }
    }
}
