package io.github.krisbitney.core.util

import io.github.krisbitney.core.resolution.Uri
import io.github.krisbitney.core.types.Client
import io.github.krisbitney.core.msgpack.EnvSerializer
import io.github.krisbitney.core.msgpack.msgPackEncode

/**
 * Returns the environment associated with the first URI in the provided history for which the client has a corresponding
 * environment.
 *
 * @param uriHistory the history of URIs to search through
 * @param client the client used to retrieve the environment by URI
 * @return the first environment in the history for which the client has a corresponding environment, or null if none is found
 */
fun getEnvFromUriHistory(uriHistory: List<Uri>, client: Client): ByteArray? {
    for (uri in uriHistory) {
        val env = client.getEnvByUri(uri)

        if (env != null) {
            return msgPackEncode(EnvSerializer, env)
        }
    }

    return null
}
