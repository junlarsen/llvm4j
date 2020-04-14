package dev.supergrecko.kllvm.utils

import dev.supergrecko.kllvm.internal.util.toBoolean
import dev.supergrecko.kllvm.internal.util.toInt
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
