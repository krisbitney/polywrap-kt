import io.github.krisbitney.core.types.Invoker
import io.github.krisbitney.core.msgpack.msgPackDecode
import io.github.krisbitney.core.msgpack.msgPackEncode
import io.github.krisbitney.plugin.PluginFactory
import io.github.krisbitney.plugin.PluginMethod
import io.github.krisbitney.plugin.PluginModule
import io.github.krisbitney.plugin.PluginPackage
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer

val mockPlugin: PluginFactory<MockPlugin.Config?> = { config: MockPlugin.Config? ->
    PluginPackage(
        pluginModule = MockPlugin(config),
        manifest = mockManifest
    )
}

class MockPlugin(config: Config? = null) : Module<MockPlugin.Config?>(config) {
    class Config()

    override suspend fun add(args: ArgsAdd, invoker: Invoker): Int {
        return args.num + args.ber
    }

    override suspend fun concat(args: ArgsConcat, invoker: Invoker): String {
        return args.str + args.ing
    }
}

@Serializable
data class ArgsAdd(
    val num: Int,
    val ber: Int
)

@Serializable
data class ArgsConcat(
    val str: String,
    val ing: String
)

@Suppress("UNUSED_PARAMETER")
abstract class Module<TConfig>(config: TConfig) : PluginModule<TConfig>(config) {

    final override val methods: Map<String, PluginMethod> = mapOf(
        "add" to ::__add,
        "concat" to ::__concat
    )

    abstract suspend fun add(
        args: ArgsAdd,
        invoker: Invoker
    ): Int

    abstract suspend fun concat(
        args: ArgsConcat,
        invoker: Invoker
    ): String

    private suspend fun __add(
        encodedArgs: ByteArray?,
        encodedEnv: ByteArray?,
        invoker: Invoker
    ): ByteArray {
        val args: ArgsAdd = encodedArgs?.let { msgPackDecode(serializer<ArgsAdd>(), it).getOrNull() }
            ?: throw Error("Missing args in invocation to plugin method 'get'")
        val response = add(args, invoker)
        return msgPackEncode(serializer(), response)
    }

    private suspend fun __concat(
        encodedArgs: ByteArray?,
        encodedEnv: ByteArray?,
        invoker: Invoker
    ): ByteArray {
        val args: ArgsConcat = encodedArgs?.let { msgPackDecode(serializer<ArgsConcat>(), it).getOrNull() }
            ?: throw Error("Missing args in invocation to plugin method 'post'")
        val response = concat(args, invoker)
        return msgPackEncode(serializer(), response)
    }
}
