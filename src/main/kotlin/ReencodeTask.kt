package eden.vidette

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.help
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.help
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.double
import com.github.ajalt.clikt.parameters.types.file
import java.io.File

class ReencodeTask : CliktCommand(name = "vidette") {
    val rootPath: File by argument().file()
        .help("Path to recursively search from")
    val bitrateThreshold: Double by option("--bitrate-threshold", "-b").double().default(2.5)
        .help("Bitrate in MB/s. Re-encoding is attempted for any file above this threshold")
    val minimumCompression: Double by option("--minimum-compression", "-c").double().default(0.66)
        .help("If re-encoding fails to achieve at least this compression factor the original file is left unchanged. e.g. 0.5 = re-encoded version must be no more than half the original size")
    val verbose: Boolean by option().flag(default = false)
        .help("More verbose output, including full output from ffmpeg")

    private val allCriteria: List<Criteria> by lazy {
        listOf(
            SufficientCompressionCriteria(minimumCompression),
            NoContentLostCriteria()
        )
    }
    private val runner: CommandRunner by lazy {
        CommandRunner(verbose)
    }

    override fun run() {
        val files = VideoFile.allIn(rootPath, runner)
        val highBitrateFiles = getHighBitrateFiles(files)

        var reencodeCount = 0
        for (file in highBitrateFiles.files) {
            if (processFile(file)) {
                reencodeCount++
            }
        }
        println("Re-encoded $reencodeCount files")
    }

    private fun getHighBitrateFiles(files: List<VideoFile>): FileEnumerator {
        val highBitrateFiles = files.filter { it.bitrate > bitrateThreshold }
        println("${highBitrateFiles.size} have too high a bit rate")
        return FileEnumerator(highBitrateFiles)
    }

    // Returns true if re-encoding succeeded
    private fun processFile(file: VideoFile): Boolean {
        print("$MOVIE_EMOJI '${file.file.name}' (${file.sizeInMB} MB) ... ")
        reencode(file)

        val output = VideoFile(file.tempSibling, runner)
        for (criteria in allCriteria) {
            val result = criteria.check(file, output)
            if (result is CheckResult.Failure) {
                println("☹  (${result.message}, discarded)")
                output.trash()
                return false
            }
        }

        val newFile = file.replaceWith(file.tempSibling)
        println("☻  (now: ${newFile.sizeInMB} MB)")
        return true
    }

    private fun reencode(file: VideoFile) {
        runner.runCommand("ffmpeg",
            "-nostdin",
            "-i", file.absolutePath,
            "-vcodec", "libx264",
            "-acodec", "copy",
            file.tempSibling.absolutePath
        )
    }
}