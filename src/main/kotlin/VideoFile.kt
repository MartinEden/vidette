package eden.vidette

import java.io.File
import kotlin.math.roundToLong

class VideoFile(val file: File, private val runner: CommandRunner) {
    // Bitrate in MB/s
    val bitrate: Double by lazy {
        (sizeInBytes / durationInSeconds) / (1000 * 1000)
    }

    val sizeInBytes: Long by lazy {
        file.length()
    }
    val sizeInMB get() = (sizeInBytes.toDouble() / (1000 * 1000)).toInt()

    val durationInSeconds by lazy {
        val rawDuration = runner.runCommand("mediainfo", "--Inform=Video;%Duration%,", file.absolutePath)
        val milliseconds = rawDuration.trim(',').toInt()
        milliseconds.toDouble() / 1000.0
    }

    val absolutePath: String get() = file.absolutePath

    fun targetLength(targetCompression: Double) = (sizeInBytes * targetCompression).roundToLong()

    val tempSibling: File by lazy {
        File(file.parentFile, file.nameWithoutExtension + ".tmp.mp4")
    }

    fun replaceWith(newFile: File): VideoFile {
        this.trash()
        newFile.renameTo(file)
        return VideoFile(file, runner)
    }

    fun trash() {
        runner.runCommand("gio", "trash", file.absolutePath)
    }

    companion object {
        fun allIn(root: File, runner: CommandRunner): List<VideoFile> {
            println("Searching ${root.absolutePath}")
            val files = root
                .walk()
                .filter { it.extension == "mp4" }
                .map { VideoFile(it, runner) }
                .toList()
            println("Found ${files.size} mp4 files")
            return files
        }
    }
}