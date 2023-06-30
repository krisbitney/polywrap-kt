package io.github.krisbitney.configBuilder

import io.github.krisbitney.core.resolution.*
import io.github.krisbitney.core.types.ClientConfig
import io.github.krisbitney.core.types.WrapEnv
import io.github.krisbitney.uriResolvers.RecursiveResolver
import io.github.krisbitney.uriResolvers.SequentialResolver
import io.github.krisbitney.uriResolvers.cache.BasicResolutionResultCache
import io.github.krisbitney.uriResolvers.cache.ResolutionResultCache
import io.github.krisbitney.uriResolvers.cache.ResolutionResultCacheResolver
import io.github.krisbitney.uriResolvers.embedded.PackageRedirect
import io.github.krisbitney.uriResolvers.embedded.StaticResolver
import io.github.krisbitney.uriResolvers.embedded.UriRedirect
import io.github.krisbitney.uriResolvers.embedded.WrapperRedirect
import io.github.krisbitney.uriResolvers.extendable.ExtendableUriResolver

/**
 * A concrete implementation of the [BaseConfigBuilder] class.
 * This class builds [ClientConfig] instances using provided configurations.
 */
class ConfigBuilder : BaseConfigBuilder() {

    /**
     * Adds the default configuration bundle to the current configuration.
     *
     * @return This [ConfigBuilder] instance for chaining calls.
     */
    override fun addDefaults(): IConfigBuilder {
        return add(DefaultBundle.getConfig())
    }

    /**
     * Builds a [ClientConfig] instance using the current configuration and an optional [ResolutionResultCache].
     *
     * @param cache An optional [ResolutionResultCache] to be used by the [ClientConfig] instance.
     * @return A [ClientConfig] instance based on the current configuration.
     */
    override fun build(cache: ResolutionResultCache?): ClientConfig {
        val static = StaticResolver.from(
            buildRedirects() + buildWrappers() + buildPackages()
        )
        return ClientConfig(
            envs = buildEnvs(),
            interfaces = buildInterfaces(),
            resolver = RecursiveResolver(
                ResolutionResultCacheResolver(
                    SequentialResolver(
                        listOf(static) + config.resolvers + listOf(ExtendableUriResolver())
                    ),
                    cache ?: BasicResolutionResultCache()
                )
            )
        )
    }

    /**
     * Builds a [ClientConfig] instance using the current configuration and a custom [UriResolver].
     *
     * @param resolver A custom [UriResolver] to be used by the [ClientConfig] instance.
     * @return A [ClientConfig] instance based on the current configuration.
     */
    override fun build(resolver: UriResolver): ClientConfig {
        return ClientConfig(
            envs = buildEnvs(),
            interfaces = buildInterfaces(),
            resolver = resolver
        )
    }

    private fun buildEnvs(): Map<Uri, WrapEnv> {
        return config.envs.mapKeys { Uri(it.key) }
    }

    private fun buildInterfaces(): Map<Uri, List<Uri>> {
        val interfaces = mutableMapOf<Uri, List<Uri>>()

        for ((uri, impls) in config.interfaces) {
            val uriImpls = impls.map { Uri(it) }
            interfaces[Uri(uri)] = uriImpls
        }

        return interfaces
    }

    private fun buildRedirects(): List<UriRedirect> {
        return config.redirects.map { (uri, redirect) ->
            Uri(uri) to Uri(redirect)
        }
    }

    private fun buildWrappers(): List<WrapperRedirect> {
        return config.wrappers.map { (uri, wrapper) ->
            Uri(uri) to wrapper
        }
    }

    private fun buildPackages(): List<PackageRedirect> {
        return config.packages.map { (uri, wrapPackage) ->
            Uri(uri) to wrapPackage
        }
    }
}
