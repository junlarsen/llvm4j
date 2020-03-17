package dev.supergrecko.kllvm.core.values

import dev.supergrecko.kllvm.core.LLVMType
import dev.supergrecko.kllvm.core.enumerations.LLVMValueKind
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class LLVMValueTest {
    @Test
    fun `creating const all one type works`() {
        val type = LLVMType.createInteger(32)
        val value = type.createConstAllOnes()

        assertEquals(LLVMValueKind.ConstantInt, value.getValueKind())
    }

    @Test
    fun `creating zero value works`() {
        val type = LLVMType.createInteger(32)
        val value = type.createZeroValue()

        assertEquals(LLVMValueKind.ConstantInt, value.getValueKind())
    }

    @Test
    fun `null pointer creation works`() {
        val type = LLVMType.createInteger(32)
        val nullptr = type.createConstPointerNull()

        assertEquals(LLVMValueKind.ConstantPointerNull, nullptr.getValueKind())
    }

    @Test
    fun `creation of undefined type object works`() {
        val type = LLVMType.createInteger(1032)
        val undef = type.createUndefined()

        assertEquals(LLVMValueKind.UndefValue, undef.getValueKind())
    }
}