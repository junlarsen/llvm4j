package dev.supergrecko.vexe.test

import java.io.File

public class TestCase(
    public val name: String,
    public val execute: TestCase.() -> Unit
) {
    internal var onTearDown: (() -> Unit)? = null
    internal var onSetup: (() -> Unit)? = null
    internal val files: MutableList<File> = mutableListOf()

    /**
     * Get a file which will only exist for the current test case.
     *
     * File is automatically erased and deleted after test case lifecycle is
     * over
     */
    public fun getTemporaryFile(name: String): File {
        return File.createTempFile(name, "").also {
            files.add(it)
        }
    }
}