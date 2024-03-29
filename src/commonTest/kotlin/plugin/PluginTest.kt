package plugin

import emptyMockInvoker
import io.github.krisbitney.core.resolution.Uri
import io.github.krisbitney.core.types.InvokeOptions
import io.github.krisbitney.core.msgpack.msgPackDecode
import io.github.krisbitney.core.msgpack.msgPackEncode
import mockPlugin
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PluginPackageTest {
    @Test
    fun createWrapper() {
        val pluginPackage = mockPlugin(null)
        val result = pluginPackage.createWrapper()
        assertTrue(result.isSuccess)
    }
}

class PluginWrapperTest {

    @Test
    fun canInvokeWrapper() {
        val wrapper = mockPlugin(null).createWrapper().getOrThrow()

        val invocation = InvokeOptions(
            uri = Uri("wrap://plugin/mock"),
            method = "add",
            args = msgPackEncode(mapOf("num" to 1, "ber" to 2))
        )

        val result = wrapper.invoke(invocation, emptyMockInvoker)
        assertEquals(result.exceptionOrNull(), null)

        val data = msgPackDecode<Int>(result.getOrThrow()).getOrNull()
        assertEquals(3, data)
    }
}
