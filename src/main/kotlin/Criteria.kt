package eden.vidette

interface Criteria {
    fun check(original: VideoFile, tempFile: VideoFile): CheckResult
}

sealed class CheckResult {
    data object Success : CheckResult()
    data class Failure(val message: String): CheckResult()
}

class SufficientCompressionCriteria(val minimumCompression: Double) : Criteria {
    override fun check(original: VideoFile, tempFile: VideoFile): CheckResult {
        return if (tempFile.sizeInBytes <= original.targetLength(minimumCompression)) {
            CheckResult.Success
        } else {
            CheckResult.Failure("still too big (${tempFile.sizeInMB} MB)")
        }
    }
}

class NoContentLostCriteria(val maximumContentLostInSeconds: Double) : Criteria {
    override fun check(original: VideoFile, tempFile: VideoFile): CheckResult {
        val diff = original.durationInSeconds - tempFile.durationInSeconds
        // If the loss of duration is tiny enough then that's just a rounding error.
        return if (diff < maximumContentLostInSeconds) {
            CheckResult.Success
        } else {
            CheckResult.Failure("some content was lost ($diff seconds)")
        }
    }
}