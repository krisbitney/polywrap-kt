package client

import io.github.krisbitney.client.PolywrapClient
import io.github.krisbitney.configBuilder.ConfigBuilder
import io.github.krisbitney.core.resolution.Uri
import io.github.krisbitney.core.resolution.UriPackageOrWrapper
import io.github.krisbitney.core.wrap.WrapManifest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class GetFileManifestTest {

    private val sha3Uri = Uri("ipfs/QmThRxFfr7Hj9Mq6WmcGXjkRrgqMG3oD93SLX27tinQWy5")
    private val config = ConfigBuilder().addDefaults().build()
    private val client = PolywrapClient(config)

    @Test
    fun tryResolveUriToPackage() {
        val result = client.tryResolveUri(uri = sha3Uri)
        assertNull(result.exceptionOrNull())
        assertTrue(result.getOrThrow() is UriPackageOrWrapper.PackageValue)
    }

    @Test
    fun getFile() {
        val result = client.getFile(sha3Uri, "wrap.info")
        assertNull(result.exceptionOrNull())

        val manifest = WrapManifest.deserialize(result.getOrThrow())
        assertNull(manifest.exceptionOrNull())

        assertEquals(manifest.getOrThrow().name, "sha3-wasm-rs")
    }

    @Test
    fun getManifest() {
        val manifest = client.getManifest(sha3Uri)
        assertNull(manifest.exceptionOrNull())
        assertEquals(manifest.getOrThrow().name, "sha3-wasm-rs")
    }
}