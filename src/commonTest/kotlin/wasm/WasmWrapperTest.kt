package wasm

import emptyMockInvoker
import io.github.krisbitney.core.resolution.Uri
import io.github.krisbitney.core.types.InvokeOptions
import io.github.krisbitney.core.msgpack.msgPackDecode
import io.github.krisbitney.core.msgpack.msgPackEncode
import io.github.krisbitney.wasm.WasmWrapper
import readTestResource
import kotlin.test.*

class WasmWrapperTest {

    private val wrapperPath = "wrappers/numbers-type/implementations/as"
    private val modulePath = "$wrapperPath/wrap.wasm"

    @Test
    fun canInvokeWrapper() {
        val wasmModule: ByteArray = readTestResource(modulePath).getOrThrow()
        val wrapper = WasmWrapper(wasmModule)

        val invocation = InvokeOptions(
            uri = Uri("wrap://ens/WasmWrapperTest/canInvokeWrapper"),
            method = "i32Method",
            args = msgPackEncode(mapOf("first" to 1, "second" to 2))
        )

        val result = wrapper.invoke(invocation, emptyMockInvoker)
        assertEquals(result.exceptionOrNull(), null)

        val data = msgPackDecode<Int>(result.getOrThrow()).getOrNull()
        assertEquals(3, data)
    }
}
