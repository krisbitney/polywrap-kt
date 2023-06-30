package io.github.krisbitney.uriResolvers.embedded

import io.github.krisbitney.core.resolution.*
import io.github.krisbitney.core.types.Client
import io.github.krisbitney.core.types.WrapPackage
import io.github.krisbitney.core.types.Wrapper

/**
 * A class that implements [UriResolver] using a predefined map of URIs to [UriPackageOrWrapper]s.
 *
 * @property uriMap A map of URIs to their corresponding [UriPackageOrWrapper]s.
 */
class StaticResolver(val uriMap: Map<String, UriPackageOrWrapper>) : UriResolver {

    companion object {

        /**
         * Creates a new [StaticResolver] instance from a list of Pair<Uri, Any> objects.
         *
         * @param staticResolverLikes A list of Pair<Uri, Any> objects to build the [StaticResolver].
         * The [Uri] is the URI to resolve, and the [Any] is either a [Uri], [WrapPackage], or [Wrapper].
         * @return A new [StaticResolver] instance with the specified URIs and corresponding [UriPackageOrWrapper]s.
         */
        fun from(staticResolverLikes: List<Pair<Uri, Any>>): StaticResolver {
            val uriMap = mutableMapOf<String, UriPackageOrWrapper>()
            for (staticResolverLike in staticResolverLikes) {
                val uri = staticResolverLike.first
                when (val item = staticResolverLike.second) {
                    is Uri -> {
                        uriMap[uri.uri] = UriPackageOrWrapper.UriValue(item)
                    }
                    is WrapPackage -> {
                        uriMap[uri.uri] = UriPackageOrWrapper.PackageValue(uri, item)
                    }
                    is Wrapper -> {
                        uriMap[uri.uri] = UriPackageOrWrapper.WrapperValue(uri, item)
                    }
                }
            }
            return StaticResolver(uriMap)
        }
    }

    /**
     * Tries to resolve the given [uri] using the predefined [uriMap].
     *
     * @param uri The URI to resolve.
     * @param client The [Client] instance used to invoke a wrapper implementing the [UriResolver] interface.
     * @param resolutionContext The current URI resolution context.
     * @return A [Result] containing a wrap package, a wrapper, or a URI if successful.
     */
    override fun tryResolveUri(
        uri: Uri,
        client: Client,
        resolutionContext: UriResolutionContext
    ): Result<UriPackageOrWrapper> {
        val uriPackageOrWrapper = uriMap[uri.uri]
        val result: Result<UriPackageOrWrapper>
        val description: String

        if (uriPackageOrWrapper != null) {
            result = Result.success(uriPackageOrWrapper)
            description = when (uriPackageOrWrapper) {
                is UriPackageOrWrapper.UriValue -> {
                    "StaticResolver - Redirect (${uri.uri} - ${uriPackageOrWrapper.uri.uri})"
                }

                is UriPackageOrWrapper.PackageValue -> {
                    "StaticResolver - Package (${uri.uri})"
                }

                is UriPackageOrWrapper.WrapperValue -> {
                    "StaticResolver - Wrapper (${uri.uri})"
                }
            }
        } else {
            result = Result.success(UriPackageOrWrapper.UriValue(uri))
            description = "StaticResolver - Miss"
        }

        resolutionContext.trackStep(
            UriResolutionStep(
                sourceUri = uri,
                result = result,
                description = description
            )
        )

        return result
    }
}
