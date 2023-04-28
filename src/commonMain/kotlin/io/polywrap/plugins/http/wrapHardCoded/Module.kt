package io.polywrap.plugins.http.wrapHardCoded

import io.polywrap.core.types.Invoker
import io.polywrap.msgpack.msgPackDecode
import io.polywrap.msgpack.msgPackEncode
import io.polywrap.plugin.PluginMethod
import io.polywrap.plugin.PluginModule
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer

@Serializable
data class ArgsGet(
    val url: String,
    val request: HttpRequest? = null
)

@Serializable
data class ArgsPost(
    val url: String,
    val request: HttpRequest? = null
)

@Suppress("UNUSED_PARAMETER", "FunctionName")
abstract class Module<TConfig>(config: TConfig) : PluginModule<TConfig>(config) {

    final override val methods: Map<String, PluginMethod> = mapOf(
        "get" to ::__get,
        "post" to ::__post
    )

    abstract suspend fun get(
        args: ArgsGet,
        invoker: Invoker
    ): Result<HttpResponse?>

    abstract suspend fun post(
        args: ArgsPost,
        invoker: Invoker
    ): Result<HttpResponse?>

    private suspend fun __get(
        encodedArgs: ByteArray?,
        invoker: Invoker,
        encodedEnv: ByteArray?
    ): ByteArray {
        val args: ArgsGet = encodedArgs?.let {
            msgPackDecode(ArgsGet.serializer(), it).getOrNull()
                ?: throw Exception("Failed to decode args in invocation to plugin method 'get'")
        } ?: throw Exception("Missing args in invocation to plugin method 'get'")
        val response = get(args, invoker).getOrThrow()
        return msgPackEncode(serializer(), response)
    }

    private suspend fun __post(
        encodedArgs: ByteArray?,
        invoker: Invoker,
        encodedEnv: ByteArray?
    ): ByteArray {
        val args: ArgsPost = encodedArgs?.let {
            msgPackDecode(ArgsPost.serializer(), it).getOrNull()
                ?: throw Exception("Failed to decode args in invocation to plugin method 'post'")
        } ?: throw Exception("Missing args in invocation to plugin method 'post'")
        val response = post(args, invoker).getOrThrow()
        return msgPackEncode(serializer(), response)
    }
}
