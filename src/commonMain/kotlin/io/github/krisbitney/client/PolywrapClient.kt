package io.github.krisbitney.client

import io.github.krisbitney.core.resolution.*
import io.github.krisbitney.core.resolution.algorithms.buildCleanUriHistory
import io.github.krisbitney.core.types.*
import io.github.krisbitney.core.util.getEnvFromUriHistory
import io.github.krisbitney.core.wrap.WrapManifest
import io.github.krisbitney.core.msgpack.EnvSerializer
import io.github.krisbitney.core.msgpack.NullableKVSerializer
import io.github.krisbitney.core.msgpack.msgPackDecode
import io.github.krisbitney.core.msgpack.msgPackEncode
import kotlinx.serialization.serializer
import io.github.krisbitney.core.resolution.algorithms.getImplementations as getImplementationsFromUri

/**
 * A client for interacting with Polywrap packages, providing high-level operations
 * such as retrieving package files, invoking package methods, and resolving URIs.
 *
 * @property config The [ClientConfig] configuration for this client instance.
 */
class PolywrapClient(val config: ClientConfig) : Client {

    override fun getInterfaces(): Map<Uri, List<Uri>>? {
        return config.interfaces
    }

    override fun getEnvs(): Map<Uri, WrapEnv>? {
        return config.envs
    }

    override fun getResolver(): UriResolver {
        return config.resolver
    }

    override fun getEnvByUri(uri: Uri): WrapEnv? {
        config.envs?.forEach { env ->
            if (env.key == uri) {
                return env.value
            }
        }
        return null
    }

    override fun getManifest(uri: Uri): Result<WrapManifest> {
        val load = loadPackage(uri)
        if (load.isFailure) {
            return Result.failure(load.exceptionOrNull()!!)
        }
        val pkg = load.getOrThrow()
        val manifest = pkg.getManifest()
        if (manifest.isFailure) {
            val exception = manifest.exceptionOrNull()!!
            val error = WrapError(
                reason = exception.message ?: "Failed to retrieve manifest",
                code = WrapErrorCode.CLIENT_GET_FILE_ERROR,
                uri = uri.uri
            )
            return Result.failure(error)
        }
        return manifest
    }

    override fun getFile(
        uri: Uri,
        path: String
    ): Result<ByteArray> {
        val load = loadPackage(uri)
        if (load.isFailure) {
            return Result.failure(load.exceptionOrNull()!!)
        }
        val pkg = load.getOrThrow()

        val result = pkg.getFile(path)

        if (result.isFailure) {
            val exception = result.exceptionOrNull()!!
            val error = WrapError(
                reason = exception.message ?: "Failed to retrieve file",
                code = WrapErrorCode.CLIENT_GET_FILE_ERROR,
                uri = uri.uri
            )
            return Result.failure(error)
        }

        return result
    }

    override fun getImplementations(
        uri: Uri,
        applyResolution: Boolean,
        resolutionContext: UriResolutionContext?
    ): Result<List<Uri>> = getImplementationsFromUri(
        uri,
        getInterfaces() ?: mapOf(),
        if (applyResolution) this else null,
        resolutionContext
    )

    override fun invokeWrapper(
        wrapper: Wrapper,
        options: InvokeOptions
    ): Result<ByteArray> = wrapper.invoke(options, this)

    override fun invokeRaw(options: InvokeOptions): Result<ByteArray> {
        val resolutionContext = options.resolutionContext ?: BasicUriResolutionContext()
        val loadResult = loadWrapper(options.uri, resolutionContext)

        if (loadResult.isFailure) {
            return Result.failure(loadResult.exceptionOrNull()!!)
        }

        val wrapper = loadResult.getOrThrow()

        val resolutionPath = resolutionContext.getResolutionPath()

        if (options.env == null) {
            val env = getEnvFromUriHistory(
                resolutionPath.ifEmpty { listOf(options.uri) },
                this@PolywrapClient
            )
            return invokeWrapper(wrapper, options.copy(env = env))
        }

        return invokeWrapper(wrapper, options)
    }

    /**
     * Invokes the wrapper at the specified URI with the provided method, arguments, and environment.
     *
     * @param uri The URI of the wrapper to be invoked.
     * @param method The method to be called on the wrapper.
     * @param args A map of arguments to be passed to the method.
     * @param env A map representing the environment to be used during the invocation.
     * @param resolutionContext The [UriResolutionContext] to be used during URI resolution, or null for a default context.
     * @return A [InvokeResult] containing the invocation result of type [R], or an error if the invocation fails.
     */
    inline fun <reified R> invoke(
        uri: Uri,
        method: String,
        args: Map<String, Any?>? = null,
        env: WrapEnv? = null,
        resolutionContext: UriResolutionContext? = null
    ): InvokeResult<R> {
        val options = InvokeOptions(
            uri = uri,
            method = method,
            args = args?.let { msgPackEncode(NullableKVSerializer, it) },
            env = env?.let { msgPackEncode(EnvSerializer, it) },
            resolutionContext = resolutionContext
        )
        return invokeRaw(options).mapCatching {
            if (R::class == Map::class) {
                msgPackDecode(NullableKVSerializer, it).getOrThrow() as R
            } else {
                msgPackDecode(serializer<R>(), it).getOrThrow()
            }
        }
    }

    /**
     * Invokes the wrapper at the specified URI with the provided method and arguments of type [T], and environment.
     *
     * @param uri The URI of the wrapper to be invoked.
     * @param method The method to be called on the wrapper.
     * @param args An instance of type [T] representing the arguments to be passed to the method.
     * @param env A map representing the environment to be used during the invocation.
     * @param resolutionContext The [UriResolutionContext] to be used during URI resolution, or null for a default context.
     * @return A [InvokeResult] containing the invocation result of type [R], or an error if the invocation fails.
     */
    inline fun <reified T, reified R> invoke(
        uri: Uri,
        method: String,
        args: T? = null,
        env: WrapEnv? = null,
        resolutionContext: UriResolutionContext? = null
    ): InvokeResult<R> {
        val options = InvokeOptions(
            uri = uri,
            method = method,
            args = args?.let { msgPackEncode(serializer<T>(), it) },
            env = env?.let { msgPackEncode(EnvSerializer, it) },
            resolutionContext = resolutionContext
        )
        return invokeRaw(options).mapCatching {
            if (R::class == Map::class) {
                msgPackDecode(NullableKVSerializer, it).getOrThrow() as R
            } else {
                msgPackDecode(serializer<R>(), it).getOrThrow()
            }
        }
    }

    override fun tryResolveUri(
        uri: Uri,
        resolutionContext: UriResolutionContext?
    ): Result<UriPackageOrWrapper> {
        val uriResolver = getResolver()
        val context = resolutionContext ?: BasicUriResolutionContext()
        return uriResolver.tryResolveUri(uri, this, context)
    }

    override fun validate(
        uri: Uri,
        abi: Boolean,
        recursive: Boolean
    ): Result<Boolean> {
        throw NotImplementedError("validate() is not yet implemented.")
    }

    override fun loadWrapper(
        uri: Uri,
        resolutionContext: UriResolutionContext?
    ): Result<Wrapper> {
        val context = resolutionContext ?: BasicUriResolutionContext()

        val result = tryResolveUri(uri, context)

        if (result.isFailure) {
            val history = buildCleanUriHistory(context.getHistory())
            val error = WrapError(
                reason = "A URI Resolver returned an error.",
                code = WrapErrorCode.URI_RESOLVER_ERROR,
                uri = uri.uri,
                resolutionStack = history,
                cause = result.exceptionOrNull()
            )
            return Result.failure(error)
        }

        return when (val uriPackageOrWrapper = result.getOrThrow()) {
            is UriPackageOrWrapper.WrapperValue -> Result.success(uriPackageOrWrapper.wrapper)
            is UriPackageOrWrapper.PackageValue -> {
                val createWrapperResult = uriPackageOrWrapper.pkg.createWrapper()

                if (createWrapperResult.isFailure) {
                    val exception = createWrapperResult.exceptionOrNull()!!
                    val error = WrapError(
                        reason = exception.message ?: "Unknown error occurred when loading wrapper",
                        code = WrapErrorCode.CLIENT_LOAD_WRAPPER_ERROR,
                        uri = uri.uri,
                        cause = exception
                    )
                    Result.failure<Wrapper>(error)
                }

                createWrapperResult
            }
            else -> {
                val message = "Unable to find URI ${uriPackageOrWrapper.uri.uri}."
                val history = buildCleanUriHistory(context.getHistory())
                val error = WrapError(
                    reason = message,
                    code = WrapErrorCode.URI_NOT_FOUND,
                    uri = uri.uri,
                    resolutionStack = history
                )
                Result.failure(error)
            }
        }
    }

    override fun loadPackage(
        uri: Uri,
        resolutionContext: UriResolutionContext?
    ): Result<WrapPackage> {
        val context = resolutionContext ?: BasicUriResolutionContext()

        val result = tryResolveUri(uri, context)

        if (result.isFailure) {
            val history = buildCleanUriHistory(context.getHistory())
            val error = WrapError(
                reason = "A URI Resolver returned an error.",
                code = WrapErrorCode.URI_RESOLVER_ERROR,
                uri = uri.uri,
                resolutionStack = history,
                cause = result.exceptionOrNull()
            )
            return Result.failure(error)
        }

        return when (val uriPackageOrWrapper = result.getOrThrow()) {
            is UriPackageOrWrapper.PackageValue -> Result.success(uriPackageOrWrapper.pkg)
            else -> {
                val message = "Unable to load package at URI ${uriPackageOrWrapper.uri.uri}."
                val history = buildCleanUriHistory(context.getHistory())
                val error = WrapError(
                    reason = message,
                    code = WrapErrorCode.URI_NOT_FOUND,
                    uri = uri.uri,
                    resolutionStack = history
                )
                Result.failure(error)
            }
        }
    }
}
