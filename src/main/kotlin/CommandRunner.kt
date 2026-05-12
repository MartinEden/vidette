package eden.vidette

class CommandRunner(val verbose: Boolean) {
    fun runCommand(vararg command: String): String {
        if (verbose) {
            println(command.joinToString(" "))
        }
        val proc = ProcessBuilder(*command)
            .redirectErrorStream(true)
            .start()
        val output = readProcessOutput(proc)
        val result = proc.waitFor()
        if (result != 0) {
            throw Exception("$this returned exit code $result. Output: $output")
        }
        return output
    }

    private fun readProcessOutput(proc: Process): String {
        val output = StringBuilder()
        var line: String?
        proc.inputReader().use { buffer ->
            while (true) {
                line = buffer.readLine()
                if (line == null) {
                    break
                }
                if (verbose) {
                    println(line)
                }
                output.append(line)
            }
        }
        return output.toString()
    }
}