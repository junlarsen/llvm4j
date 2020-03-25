package dev.supergrecko.kllvm.core.values

import dev.supergrecko.kllvm.core.enumerations.ValueKind
import dev.supergrecko.kllvm.factories.ConstantFactory
import dev.supergrecko.kllvm.factories.TypeFactory
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ValueTest {
    @Test
    fun `creating const all one type works`() {
        val type = TypeFactory.integer(32)
        val value = ConstantFactory.constAllOnes(type)

        assertEquals(ValueKind.ConstantInt, value.getValueKind())
    }

    @Test
    fun `creating zero value works`() {
        val type = TypeFactory.integer(32)
        val value = ConstantFactory.constNull(type)

        assertEquals(ValueKind.ConstantInt, value.getValueKind())
    }

    @Test
    fun `null pointer creation works`() {
        val type = TypeFactory.integer(32)
        val nullptr = ConstantFactory.constPointerNull(type)

        assertEquals(ValueKind.ConstantPointerNull, nullptr.getValueKind())
    }

    @Test
    fun `creation of undefined type object works`() {
        val type = TypeFactory.integer(1032)
        val undef = ConstantFactory.constUndef(type)

        assertEquals(ValueKind.UndefValue, undef.getValueKind())
    }
}