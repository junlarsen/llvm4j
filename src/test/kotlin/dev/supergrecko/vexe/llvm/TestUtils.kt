package dev.supergrecko.vexe.llvm

import java.io.File

internal class TestUtils {
    internal val files: MutableList<File> = mutableListOf()

    private fun getRandomString(): String {
        val chars = ('A'..'Z') + ('a'..'z')

        return (1..16).map { chars.random() }
            .joinToString("", postfix = ".tmp")
    }

    public fun destruct() {
        files.filter { it.exists() }.forEach { it.delete() }
    }

    public fun getTemporaryFile(name: String? = null): File {
        val file = name ?: getRandomString()

        return File.createTempFile(file, "").also {
            files.add(it)
        }
    }
}
