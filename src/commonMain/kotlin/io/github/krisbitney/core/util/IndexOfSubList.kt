package io.github.krisbitney.core.util

/**
 * Returns the starting position of the first occurrence of the specified
 * target list within the specified source list, or -1 if there is no
 * such occurrence. More formally, returns the lowest index {@code i}
 * such that {@code source.subList(i, i+target.size).equals(target)},
 * or -1 if there is no such index. (Returns -1 if
 * {@code target.size > source.size})
 *
 * <p>This implementation uses the "brute force" technique of scanning
 * over the source list, looking for a match with the target at each
 * location in turn.
 *
 * @param source the list in which to search for the first occurrence
 *        of {@code target}.
 * @param target the list to search for as a subList of {@code source}.
 * @return the starting position of the first occurrence of the specified
 *         target list within the specified source list, or -1 if there
 *         is no such occurrence.
 */
fun <T> List<T>.indexOfSubList(target: List<T>): Int {
    val sourceSize = this.size
    val targetSize = target.size
    val maxCandidateIndex = sourceSize - targetSize

    if (sourceSize < targetSize) return -1

    outerLoop@ for (candidateIndex in 0..maxCandidateIndex) {
        for (i in 0 until targetSize) {
            if (this[candidateIndex + i] != target[i]) continue@outerLoop
        }
        return candidateIndex
    }
    return -1
}