package dev.supergrecko.kllvm.internal

import dev.supergrecko.kllvm.internal.util.fromLLVMBool
import dev.supergrecko.kllvm.internal.util.toLLVMBool
import kotlin.test.assertEquals
import org.junit.jupiter.api.Test

class ConversionsTest {
    @Test
    fun `int to bool`() {
        assertEquals(true, 1.fromLLVMBool())
        assertEquals(false, 0.fromLLVMBool())
    }

    @Test
    fun `bool to int`() {
        assertEquals(1, true.toLLVMBool())
        assertEquals(0, false.toLLVMBool())
    }
}
