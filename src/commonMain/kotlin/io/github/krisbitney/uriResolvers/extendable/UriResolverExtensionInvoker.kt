package io.github.krisbitney.uriResolvers.extendable

import io.github.krisbitney.core.resolution.Uri
import io.github.krisbitney.core.types.InvokeOptions
import io.github.krisbitney.core.types.Invoker
import io.github.krisbitney.core.msgpack.msgPackDecode
import io.github.krisbitney.core.msgpack.msgPackEncode
import kotlinx.serialization.serializer

object UriResolverExtensionInvoker {
    /**
     * Use an invoker to try to resolve a URI using a wrapper that implements the UriResolver interface
     *
     * @param invoker - invokes the wrapper with the resolution URI as an argument
     * @param wrapper - URI for wrapper that implements the UriResolver interface
     * @param uri - the URI to resolve
     */
    fun tryResolveUri(
        invoker: Invoker,
        wrapper: Uri,
        uri: Uri
    ): Result<MaybeUriOrManifest> {
        val result = invoker.invokeRaw(
            InvokeOptions(
                uri = wrapper,
                method = "tryResolveUri",
                args = msgPackEncode(serializer(), mapOf("authority" to uri.authority, "path" to uri.path))
            )
        )
        return if (result.isFailure) {
            Result.failure(result.exceptionOrNull()!!)
        } else {
            msgPackDecode(MaybeUriOrManifest.serializer(), result.getOrThrow())
        }
    }

    /**
     * Use an invoker to fetch a file using a wrapper that implements the UriResolver interface
     *
     * @param invoker - invokes the wrapper with the filepath as an argument
     * @param wrapper - URI for wrapper that implements the UriResolver interface
     * @param path - a filepath, the format of which depends on the UriResolver
     */
    fun getFile(
        invoker: Invoker,
        wrapper: Uri,
        path: String
    ): Result<ByteArray?> {
        val result = invoker.invokeRaw(
            InvokeOptions(
                uri = wrapper,
                method = "getFile",
                args = msgPackEncode(serializer(), mapOf("path" to path))
            )
        )
        return if (result.isFailure) {
            Result.failure(result.exceptionOrNull()!!)
        } else {
            msgPackDecode(serializer(), result.getOrThrow())
        }
    }
}
