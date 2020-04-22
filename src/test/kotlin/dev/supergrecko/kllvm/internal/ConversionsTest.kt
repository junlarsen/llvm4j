package dev.supergrecko.kllvm.internal

import dev.supergrecko.kllvm.internal.util.toBoolean
import dev.supergrecko.kllvm.internal.util.toInt

import kotlin.test.assertEquals

import org.junit.jupiter.api.Test

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
