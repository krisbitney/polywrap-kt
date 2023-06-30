package io.github.krisbitney.core.types

import io.github.krisbitney.core.resolution.Uri
import io.github.krisbitney.core.resolution.UriResolutionContext
import io.github.krisbitney.core.resolution.UriResolutionHandler
import io.github.krisbitney.core.resolution.UriResolver
import io.github.krisbitney.core.wrap.WrapManifest

/** A map of string-indexed, Msgpack-serializable environmental variables associated with a wrapper */
typealias WrapEnv = Map<String, Any>

/**
 * Core Client configuration that can be passed to the PolywrapClient or PolywrapCoreClient constructors.
 * @property resolver configure URI resolution for redirects, packages, and wrappers
 * @property interfaces set environmental variables for a wrapper
 * @property envs register interface implementations
 */
data class ClientConfig(
    val resolver: UriResolver,
    val interfaces: Map<Uri, List<Uri>>? = null,
    val envs: Map<Uri, WrapEnv>? = null
)

/**
 * CoreClient invokes wrappers and interacts with wrap packages.
 */
interface Client : Invoker, UriResolutionHandler {

    /**
     * Returns the interface implementations stored in the configuration.
     *
     * @return A map of interface URIs to a list of their respective implementation URIs.
     */
    fun getInterfaces(): Map<Uri, List<Uri>>?

    /**
     * Returns all env registrations from the configuration used to instantiate the client.
     * @return an array of env objects containing wrapper environmental variables
     */
    fun getEnvs(): Map<Uri, WrapEnv>?

    /**
     * Returns an env (a set of environmental variables) from the configuration
     * used to instantiate the client.
     * @param uri the URI used to register the env
     * @return an env, or null if an env is not found at the given URI
     */
    fun getEnvByUri(uri: Uri): WrapEnv?

    /**
     * Returns the URI resolver from the configuration used to instantiate the client.
     * @return an object that implements the IUriResolver interface
     */
    fun getResolver(): UriResolver

    /**
     * Returns a package's wrap manifest.
     * @param uri a wrap URI
     * @return a Result containing the io.github.krisbitney.core.WrapManifest if the request was successful
     */
    fun getManifest(uri: Uri): Result<WrapManifest>

    /**
     * Returns a file contained in a wrap package.
     * @param uri a wrap URI
     * @param path file path from wrapper root
     * @return a Result containing a file if the request was successful
     */
    fun getFile(
        uri: Uri,
        path: String
    ): Result<ByteArray>

    /**
     * Validate a wrapper, given a URI.
     *
     * @param uri the Uri to resolve
     * @param abi validate the full ABI
     * @param recursive recursively validate imports
     * @return a Result containing a boolean or Error
     */
    fun validate(
        uri: Uri,
        abi: Boolean = false,
        recursive: Boolean = false
    ): Result<Boolean>

    /**
     * Load a wrapper, given a URI.
     *
     * @param uri the Uri to resolve
     * @param resolutionContext - Use and update an existing resolution context
     *
     * @return a Result containing a Wrapper if the request was successful
     */
    fun loadWrapper(
        uri: Uri,
        resolutionContext: UriResolutionContext? = null
    ): Result<Wrapper>

    /**
     * Load a wrap package, given a URI.
     *
     * @param uri the Uri to resolve
     * @param resolutionContext - Use and update an existing resolution context
     *
     * @return a Result containing a WrapPackage if the request was successful
     */
    fun loadPackage(
        uri: Uri,
        resolutionContext: UriResolutionContext? = null
    ): Result<WrapPackage>
}
