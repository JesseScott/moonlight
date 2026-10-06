package tt.co.jesses.moonlight.common.util

/** One library and the text of its license */
data class LicenseEntry(val name: String, val text: String)

/**
 * Reads what the oss-licenses Gradle plugin generates: [texts] holds the license texts back to back, and [metadata]
 * has one "offset:length name" line per library (libraries that share a license point at the same text, and the
 * offsets are in bytes). Lines that do not make sense are skipped. The result is sorted by name.
 */
fun parseLicenses(texts: ByteArray, metadata: String): List<LicenseEntry> =
    metadata.lineSequence().mapNotNull { line ->
        val space = line.indexOf(' ')
        if (space <= 0) return@mapNotNull null
        val range = line.substring(0, space).split(':').mapNotNull { it.toIntOrNull() }
        if (range.size != 2) return@mapNotNull null
        val (offset, length) = range
        if (offset < 0 || length < 0 || offset + length > texts.size) return@mapNotNull null
        LicenseEntry(
            name = line.substring(space + 1).trim(),
            text = String(texts, offset, length, Charsets.UTF_8).trim(),
        )
    }.sortedBy { it.name.lowercase() }.toList()
