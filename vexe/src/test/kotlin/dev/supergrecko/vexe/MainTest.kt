package dev.supergrecko.vexe

import dev.supergrecko.vexe.test.TestSuite
import kotlin.test.assertEquals

internal class MainTest : TestSuite({
    describe("Test that Hello World works") {
        val message = hello()

        assertEquals("Hello World!", message)
    }
})
