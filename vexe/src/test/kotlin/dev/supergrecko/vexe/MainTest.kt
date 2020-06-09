package dev.supergrecko.vexe

import kotlin.test.assertEquals
import org.junit.jupiter.api.Test

class MainTest {
    @Test
    fun `Test that Hello World works`() {
        val message = hello()

        assertEquals("Hello World!", message)
    }
}
