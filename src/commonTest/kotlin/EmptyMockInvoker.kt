import io.github.krisbitney.core.resolution.Uri
import io.github.krisbitney.core.resolution.UriResolutionContext
import io.github.krisbitney.core.types.InvokeOptions
import io.github.krisbitney.core.types.Invoker
import io.github.krisbitney.core.types.Wrapper

val emptyMockInvoker = object : Invoker {
    override fun invokeWrapper(wrapper: Wrapper, options: InvokeOptions): Result<ByteArray> {
        throw NotImplementedError()
    }

    override fun invokeRaw(options: InvokeOptions): Result<ByteArray> {
        throw NotImplementedError()
    }

    override fun getImplementations(
        uri: Uri,
        applyResolution: Boolean,
        resolutionContext: UriResolutionContext?
    ): Result<List<Uri>> {
        throw NotImplementedError()
    }
}
