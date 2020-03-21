package dev.supergrecko.kllvm.core.values

import dev.supergrecko.kllvm.core.enumerations.LLVMValueKind
import dev.supergrecko.kllvm.factories.ConstantFactory
import dev.supergrecko.kllvm.factories.TypeFactory
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class LLVMValueTest {
    @Test
    fun `creating const all one type works`() {
        val type = TypeFactory.integer(32)
        val value = ConstantFactory.constAllOnes(type)

        assertEquals(LLVMValueKind.ConstantInt, value.getValueKind())
    }

    @Test
    fun `creating zero value works`() {
        val type = TypeFactory.integer(32)
        val value = ConstantFactory.constNull(type)

        assertEquals(LLVMValueKind.ConstantInt, value.getValueKind())
    }

    @Test
    fun `null pointer creation works`() {
        val type = TypeFactory.integer(32)
        val nullptr = ConstantFactory.constPointerNull(type)

        assertEquals(LLVMValueKind.ConstantPointerNull, nullptr.getValueKind())
    }

    @Test
    fun `creation of undefined type object works`() {
        val type = TypeFactory.integer(1032)
        val undef = ConstantFactory.constUndef(type)

        assertEquals(LLVMValueKind.UndefValue, undef.getValueKind())
    }
}