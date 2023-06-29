package io.github.krisbitney.configBuilder

import io.github.krisbitney.client.PolywrapClient

fun configBuilder(configBuilder: ClientConfigBuilder? = null, configure: ClientConfigBuilder.() -> Unit): ClientConfigBuilder {
    val builder = configBuilder ?: ClientConfigBuilder()
    return builder.apply(configure)
}

fun polywrapClient(configBuilder: ClientConfigBuilder? = null, configure: ClientConfigBuilder.() -> Unit): PolywrapClient {
    val config = configBuilder(configBuilder, configure).build()
    return PolywrapClient(config)
}
