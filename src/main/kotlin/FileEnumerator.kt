package eden.vidette

import java.io.File

class FileEnumerator(val list: List<VideoFile>) {
    val files : Sequence<VideoFile> = sequence {
        var lastFolder: File? = null
        for (file in list) {
            if (file.file.parentFile != lastFolder) {
                lastFolder = file.file.parentFile
                println("Switching directory to ${lastFolder.absolutePath}")
            }
            yield(file)
        }
    }
}