package io.github.krisbitney.configBuilder

import io.github.krisbitney.core.resolution.UriResolver
import io.github.krisbitney.core.types.ClientConfig
import io.github.krisbitney.core.types.WrapPackage
import io.github.krisbitney.core.types.Wrapper
import io.github.krisbitney.core.types.WrapEnv
import io.github.krisbitney.uriResolvers.cache.ResolutionResultCache
import kotlin.collections.List
import kotlin.collections.Map

/**
 * Interface defining a builder for creating [ClientConfig] instances with support for
 * adding, removing, and modifying various configuration options.
 */
interface IConfigBuilder {
    /**
     * The [BuilderConfig] instance representing the current configuration state.
     */
    val config: BuilderConfig

    /**
     * Builds a [ClientConfig] instance with the current builder configuration and an optional [ResolutionResultCache].
     *
     * @param cache Optional [ResolutionResultCache] to be used with the created [ClientConfig] instance.
     * @return A [ClientConfig] instance with the specified configuration.
     */
    fun build(cache: ResolutionResultCache? = null): ClientConfig

    /**
     * Builds a [ClientConfig] instance with the current builder configuration and a required [UriResolver].
     *
     * @param resolver Required [UriResolver] to be used with the created [ClientConfig] instance.
     * @return A [ClientConfig] instance with the specified configuration.
     */
    fun build(resolver: UriResolver): ClientConfig

    /**
     * Adds the given [BuilderConfig] to the current configuration.
     *
     * @param config The [BuilderConfig] to add.
     * @return This [IConfigBuilder] instance for chaining calls.
     */
    fun add(config: BuilderConfig): IConfigBuilder

    /**
     * Adds default configuration bundle to the current configuration.
     *
     * @return This [IConfigBuilder] instance for chaining calls.
     */
    fun addDefaults(): IConfigBuilder

    /**
     * Adds a wrapper with a specified URI key to the current configuration.
     *
     * @param wrapper A [Pair] of the URI key and the [Wrapper] to add.
     * @return This [IConfigBuilder] instance for chaining calls.
     */
    fun addWrapper(wrapper: Pair<String, Wrapper>): IConfigBuilder

    /**
     * Adds a set of wrappers with specified URI keys to the current configuration.
     *
     * @param wrappers A [Map] of URI keys to [Wrapper] instances to add.
     * @return This [IConfigBuilder] instance for chaining calls.
     */
    fun addWrappers(wrappers: Map<String, Wrapper>): IConfigBuilder

    /**
     * Removes a wrapper with the specified URI key from the current configuration.
     *
     * @param uri The URI key of the wrapper to remove.
     * @return This [IConfigBuilder] instance for chaining calls.
     */
    fun removeWrapper(uri: String): IConfigBuilder

    /**
     * Adds a package with a specified URI key to the current configuration.
     *
     * @param wrapPackage A [Pair] of the URI key and the [WrapPackage] to add.
     * @return This [IConfigBuilder] instance for chaining calls.
     */
    fun addPackage(wrapPackage: Pair<String, WrapPackage>): IConfigBuilder

    /**
     * Adds a set of packages with specified URI keys to the current configuration.
     *
     * @param packages A [Map] of URI keys to [WrapPackage] instances to add.
     * @return This [IConfigBuilder] instance for chaining calls.
     */
    fun addPackages(packages: Map<String, WrapPackage>): IConfigBuilder

    /**
     * Removes a package with the specified URI key from the current configuration.
     *
     * @param uri The URI key of the package to remove.
     * @return This [IConfigBuilder] instance for chaining calls.
     */
    fun removePackage(uri: String): IConfigBuilder

    /**
     * Adds an environment variable with a specified URI key to the current configuration.
     *
     * @param env A [Pair] of the URI key and the [WrapEnv] to add.
     * @return This [IConfigBuilder] instance for chaining calls.
     */
    fun addEnv(env: Pair<String, WrapEnv>): IConfigBuilder

    /**
     * Adds a set of environment variables with specified URI keys to the current configuration.
     *
     * @param envs A [Map] of URI keys to [WrapEnv] instances to add.
     * @return This [IConfigBuilder] instance for chaining calls.
     */
    fun addEnvs(envs: Map<String, WrapEnv>): IConfigBuilder

    /**
     * Removes an environment variable with the specified URI key from the current configuration.
     *
     * @param uri The URI key of the environment variable to remove.
     * @return This [IConfigBuilder] instance for chaining calls.
     */
    fun removeEnv(uri: String): IConfigBuilder

    /**
     * Sets or replaces an environment variable with a specified URI key in the current configuration.
     *
     * @param env A [Pair] of the URI key and the [WrapEnv] to set.
     * @return This [IConfigBuilder] instance for chaining calls.
     */
    fun setEnv(env: Pair<String, WrapEnv>): IConfigBuilder

    /**
     * Adds an interface implementation with the specified interface and implementation URIs.
     *
     * @param interfaceUri The URI of the interface to associate with the implementation.
     * @param implementationUri The URI of the implementation.
     * @return This [IConfigBuilder] instance for chaining calls.
     */
    fun addInterfaceImplementation(interfaceUri: String, implementationUri: String): IConfigBuilder

    /**
     * Adds multiple interface implementations with the specified interface URI and a list of implementation URIs.
     *
     * @param interfaceUri The URI of the interface to associate with the implementations.
     * @param implementationUris A [List] of URIs of the implementations.
     * @return This [IConfigBuilder] instance for chaining calls.
     */
    fun addInterfaceImplementations(interfaceUri: String, implementationUris: List<String>): IConfigBuilder

    /**
     * Removes an interface implementation with the specified interface and implementation URIs.
     *
     * @param interfaceUri The URI of the interface associated with the implementation.
     * @param implementationUri The URI of the implementation to remove.
     * @return This [IConfigBuilder] instance for chaining calls.
     */
    fun removeInterfaceImplementation(interfaceUri: String, implementationUri: String): IConfigBuilder

    /**
     * Adds a redirect with a specified source and destination URI.
     *
     * @param redirect A [Pair] of the source URI and the destination URI.
     * @return This [IConfigBuilder] instance for chaining calls.
     */
    fun addRedirect(redirect: Pair<String, String>): IConfigBuilder

    /**
     * Adds a set of redirects with specified source and destination URIs.
     *
     * @param redirects A [Map] of source URIs to destination URIs.
     * @return This [IConfigBuilder] instance for chaining calls.
     */
    fun addRedirects(redirects: Map<String, String>): IConfigBuilder

    /**
     * Removes a redirect with the specified source URI.
     *
     * @param from The source URI of the redirect to remove.
     * @return This [IConfigBuilder] instance for chaining calls.
     */
    fun removeRedirect(from: String): IConfigBuilder

    /**
     * Adds a [UriResolver] to the current configuration.
     *
     * @param resolver The [UriResolver] instance to add.
     * @return This [IConfigBuilder] instance for chaining calls.
     */
    fun addResolver(resolver: UriResolver): IConfigBuilder

    /**
     * Adds a list of [UriResolver] instances to the current configuration.
     *
     * @param resolvers A [List] of [UriResolver] instances to add.
     * @return This [IConfigBuilder] instance for chaining calls.
     */
    fun addResolvers(resolvers: List<UriResolver>): IConfigBuilder
}
