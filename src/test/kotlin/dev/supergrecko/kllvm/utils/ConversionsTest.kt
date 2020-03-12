package dev.supergrecko.kllvm.utils

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ConversionsTest {
    @Test
    fun `int to bool`() {
        assertEquals(true, 1.toBoolean())
        assertEquals(false, 0.toBoolean())
    }

    @Test
    fun `bool to int`() {
        assertEquals(1, true.toInt())
        assertEquals(0, false.toInt())
    }
}