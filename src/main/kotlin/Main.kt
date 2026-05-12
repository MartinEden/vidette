package eden.vidette

import com.github.ajalt.clikt.core.main

const val MOVIE_EMOJI = "🎥"

fun main(args: Array<String>) {
    ReencodeTask().main(args)
}
