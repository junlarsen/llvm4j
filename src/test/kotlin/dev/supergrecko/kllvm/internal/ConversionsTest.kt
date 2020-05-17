package dev.supergrecko.kllvm.internal

import dev.supergrecko.kllvm.internal.util.fromLLVMBool
import dev.supergrecko.kllvm.internal.util.toLLVMBool
import kotlin.test.assertEquals
import org.junit.jupiter.api.Test

class ConversionsTest {
    @Test
    fun `Conversion from Int to Bool via extension`() {
        assertEquals(true, 1.fromLLVMBool())
        assertEquals(false, 0.fromLLVMBool())
    }

    @Test
    fun `Conversion from Bool to Int via extension`() {
        assertEquals(1, true.toLLVMBool())
        assertEquals(0, false.toLLVMBool())
    }

    @Test
    fun `Negative number is false`() {
        assertEquals(false, (-100).fromLLVMBool())
    }

    @Test
    fun `Any positive number is true`() {
        assertEquals(true, 1238182.fromLLVMBool())
    }
}
