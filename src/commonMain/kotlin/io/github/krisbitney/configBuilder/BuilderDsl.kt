package io.github.krisbitney.configBuilder

import io.github.krisbitney.client.PolywrapClient

fun configBuilder(configBuilder: ConfigBuilder? = null, configure: ConfigBuilder.() -> Unit): ConfigBuilder {
    val builder = configBuilder ?: ConfigBuilder()
    return builder.apply(configure)
}

fun polywrapClient(configBuilder: ConfigBuilder? = null, configure: ConfigBuilder.() -> Unit): PolywrapClient {
    val config = configBuilder(configBuilder, configure).build()
    return PolywrapClient(config)
}
