package dev.supergrecko.kllvm.ir

import dev.supergrecko.kllvm.ir.types.IntType
import kotlin.test.assertEquals
import org.junit.jupiter.api.Test

class ValueTest {
    @Test
    fun `creating const all one type works`() {
        val type = IntType(32)
        val value = type.getConstantAllOnes()

        assertEquals(ValueKind.ConstantInt, value.getValueKind())
    }

    @Test
    fun `creating zero value works`() {
        val type = IntType(32)
        val value = type.getConstantNull()

        assertEquals(ValueKind.ConstantInt, value.getValueKind())
    }

    @Test
    fun `null pointer creation works`() {
        val type = IntType(32)
        val nullptr = type.getConstantNullPointer()

        assertEquals(ValueKind.ConstantPointerNull, nullptr.getValueKind())
    }

    @Test
    fun `creation of undefined type object works`() {
        val type = IntType(1032)
        val undef = type.getConstantUndef()

        assertEquals(ValueKind.UndefValue, undef.getValueKind())
    }
}
