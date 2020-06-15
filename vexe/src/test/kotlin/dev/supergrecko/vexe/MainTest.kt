package dev.supergrecko.vexe

import dev.supergrecko.vexe.test.TestSuite
import kotlin.test.assertEquals
import org.junit.jupiter.api.Test

internal class MainTest : TestSuite({
    describe("Test that Hello World works") {
        val message = hello()

        assertEquals("Hello World!", message)
    }
})
