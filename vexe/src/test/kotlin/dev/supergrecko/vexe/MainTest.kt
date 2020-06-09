package dev.supergrecko.vexe

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class MainTest {
    @Test
    fun `Test that Hello World works`() {
        val message = hello()

        assertEquals("Hello World!", message)
    }
}
