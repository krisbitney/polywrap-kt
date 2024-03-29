package io.github.krisbitney.plugin

import io.github.krisbitney.core.types.Invoker

/**
 * An abstract class for plugin modules with a generic configuration type [TConfig].
 * This class is extended by the generated plugin module class.
 *
 * @param TConfig The type of the configuration object for the plugin module.
 * @property config The configuration object for the plugin module.
 */
abstract class PluginModule<TConfig>(val config: TConfig) {

    /**
     * An abstract property representing a map of method names to their corresponding [PluginMethod] instances.
     */
    abstract val methods: Map<String, PluginMethod>

    /**
     * A suspend function for wrapping the invocation of a plugin method.
     * @param method The name of the method to invoke.
     * @param args The input arguments as a byte array.
     * @param env The environment as a byte array.
     * @param invoker The [Invoker] instance to be used during the invocation.
     * @return A [Result] instance containing either the result as a byte array or an error if the invocation fails.
     */
    suspend fun wrapInvoke(
        method: String,
        args: ByteArray?,
        invoker: Invoker,
        env: ByteArray?
    ): Result<ByteArray> {
        val fn = methods[method] ?: return Result.failure(Error("Plugin missing method \"$method\""))
        return runCatching { fn(args, env, invoker) }
    }
}
