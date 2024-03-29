package io.github.krisbitney.core.resolution.algorithms

import io.github.krisbitney.core.resolution.UriPackageOrWrapper
import io.github.krisbitney.core.resolution.UriResolutionStep

/**
 * Represents a clean resolution step in the URI resolution history.
 */
typealias CleanResolutionStep = String

/**
 * Builds a clean URI history from a given URI resolution history, optionally up to a specified depth.
 *
 * @param history The URI resolution history to clean.
 * @param depth The maximum depth to traverse in the URI resolution history.
 * @return A list representing the clean URI resolution history.
 */
fun buildCleanUriHistory(history: List<UriResolutionStep>, depth: Int? = null): CleanResolutionStep {
    val cleanHistory = mutableListOf<String>()

    val currentDepth = depth?.dec()

    if (history.isEmpty()) {
        return cleanHistory.toString()
    }

    for (step in history) {
        val from = step.sourceUri.uri

        if (step.result.isSuccess) {
            val uriPackageOrWrapper = step.result.getOrThrow()
            val to = uriPackageOrWrapper.uri

            when (uriPackageOrWrapper) {
                is UriPackageOrWrapper.UriValue -> {
                    if (from == to.uri) {
                        cleanHistory.add(
                            step.description?.let {
                                "$from => $it"
                            } ?: from
                        )
                    } else {
                        cleanHistory.add(
                            step.description?.let {
                                "$from => $it => uri ($to)"
                            } ?: "$from => uri ($to)"
                        )
                    }
                }
                is UriPackageOrWrapper.PackageValue -> {
                    cleanHistory.add(
                        step.description?.let {
                            "$from => $it => package ($to)"
                        } ?: "$from => package ($to)"
                    )
                }
                is UriPackageOrWrapper.WrapperValue -> {
                    cleanHistory.add(
                        step.description?.let {
                            "$from => $it => wrapper ($to)"
                        } ?: "$from => wrapper ($to)"
                    )
                }
            }
        } else {
            val message = step.result.exceptionOrNull()?.message
            cleanHistory.add(
                step.description?.let {
                    "$from => $it => error ($message)"
                } ?: "$from => error ($message)"
            )
        }

        if (step.subHistory.isNullOrEmpty() || (currentDepth != null && currentDepth < 0)) {
            continue
        }

        val subHistory = buildCleanUriHistory(step.subHistory, currentDepth)
        if (subHistory.isNotEmpty()) {
            cleanHistory.add(subHistory)
        }
    }

    return cleanHistory.joinToString("\n")
}
