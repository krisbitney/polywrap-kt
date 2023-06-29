package io.github.krisbitney.configBuilder

import io.github.krisbitney.configBuilder.embeds.getIpfsHttpClientWrap
import io.github.krisbitney.configBuilder.embeds.getIpfsResolverWrap
import io.github.krisbitney.core.resolution.Uri
import io.github.krisbitney.core.types.WrapPackage
import io.github.krisbitney.plugins.filesystem.fileSystemPlugin
import io.github.krisbitney.plugins.http.httpPlugin
import io.github.krisbitney.uriResolvers.embedded.UriRedirect
import io.github.krisbitney.uriResolvers.extendable.ExtendableUriResolver

class DefaultBundle {

    interface IDefaultEmbed {
        val uri: Uri
        val pkg: WrapPackage
        val source: Uri
    }

    interface IDefaultPlugin {
        val uri: Uri
        val plugin: WrapPackage
        val implements: List<Uri>
    }

    companion object {
        val ipfsProviders: List<String> = listOf(
            "https://ipfs.wrappers.io",
            "https://ipfs.io"
        )

        val embeds: Map<String, IDefaultEmbed> = mapOf(
            "ipfsHttpClient" to object : IDefaultEmbed {
                override val uri = Uri("embed/ipfs-http-client@1.0.0")
                override val pkg = getIpfsHttpClientWrap()
                override val source = Uri("ens/wraps.eth:ipfs-http-client@1.0.0")
            },
            "ipfsResolver" to object : IDefaultEmbed {
                override val uri = Uri("embed/async-ipfs-uri-resolver-ext@1.0.0")
                override val pkg = getIpfsResolverWrap()
                override val source = Uri("ens/wraps.eth:async-ipfs-uri-resolver-ext@1.0.0")
            }
        )

        val textRecordResolverRedirect: UriRedirect =
            Uri("ens/wraps.eth:ens-text-record-uri-resolver-ext@1.0.0") to Uri("ipfs/QmaM318ABUXDhc5eZGGbmDxkb2ZgnbLxigm5TyZcCsh1Kw")

        val uriResolverExts: List<Uri> = listOf(
            embeds["ipfsResolver"]!!.source,
//            textRecordResolverRedirect.first,
            Uri("wrap://ipfs/QmbsxmNDbJ3uNp9CRBTfTVQJikCYWQrFcXbC3mVpTRTLZg"), // Uri("ens/wraps.eth:http-uri-resolver-ext@1.0.0"),
            Uri("wrap://ipfs/QmQLEc9G4YnMxLexgkWzQo1jHVhfmdLQWX7zXm4Kh9RPMU") // Uri("ens/wraps.eth:file-system-uri-resolver-ext@1.0.0"),
//            Uri("ens/wraps.eth:ens-uri-resolver-ext@1.0.0"),
//            Uri("ens/wraps.eth:ens-ipfs-contenthash-uri-resolver-ext@1.0.0"),
//            Uri("ens/wraps.eth:ens-ocr-contenthash-uri-resolver-ext@1.0.0")
        )

        val plugins: Map<String, IDefaultPlugin> = mapOf(
            "http" to object : IDefaultPlugin {
                override val uri = Uri("plugin/http@1.1.0")
                override val plugin = httpPlugin(null)
                override val implements = listOf(
                    Uri("ens/wraps.eth:http@1.1.0"),
                    Uri("ens/wraps.eth:http@1.0.0")
                )
            },
            "fileSystem" to object : IDefaultPlugin {
                override val uri = Uri("plugin/file-system@1.0.0")
                override val plugin = fileSystemPlugin(null)
                override val implements = listOf(
                    Uri("ens/wraps.eth:file-system@1.0.0")
                )
            }
        )

        /**
         * Get the default Client configuration bundle
         */
        fun getConfig(): BuilderConfig {
            val builder = ClientConfigBuilder()

            // Add all embedded packages
            for (embed in embeds.values) {
                builder.addPackage(embed.uri.uri to embed.pkg)

                // Add source redirect
                builder.addRedirect(embed.source.uri to embed.uri.uri)

                // Add source implementation
                builder.addInterfaceImplementation(embed.source.uri, embed.uri.uri)
            }

            for (plugin in plugins.values) {
                builder.addPackage(plugin.uri.uri to plugin.plugin)

                // Add all interface implementations & redirects
                for (interfaceUri in plugin.implements) {
                    builder.addInterfaceImplementation(interfaceUri.uri, plugin.uri.uri)
                    builder.addRedirect(interfaceUri.uri to plugin.uri.uri)
                }
            }

            // Add all uri-resolver-ext interface implementations
            builder.addInterfaceImplementations(
                ExtendableUriResolver.defaultExtInterfaceUris[0].uri,
                uriResolverExts.map { it.uri }
            )
            builder.addRedirect(textRecordResolverRedirect.first.uri to textRecordResolverRedirect.second.uri)

            // Configure the ipfs-uri-resolver provider endpoints & retry counts
            builder.addEnv(
                embeds["ipfsResolver"]!!.source.uri to
                    mapOf(
                        "provider" to ipfsProviders[0],
                        "fallbackProviders" to ipfsProviders.slice(1 until ipfsProviders.size),
                        "retries" to mapOf("tryResolveUri" to 2, "getFile" to 2),
                        "timeout" to 10000
                    )
            )

            return builder.config
        }
    }
}
