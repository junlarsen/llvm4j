package dev.supergrecko.kllvm.ir

import dev.supergrecko.kllvm.ir.types.IntType
import dev.supergrecko.kllvm.ir.values.constants.ConstantInt
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.junit.jupiter.api.Test

class ValueTest {
    @Test
    fun `creating const all one type`() {
        val type = IntType(32)
        val value = type.getConstantAllOnes()

        assertEquals(ValueKind.ConstantInt, value.getValueKind())
    }

    @Test
    fun `creating zero value`() {
        val type = IntType(32)
        val value = type.getConstantNull()

        assertEquals(ValueKind.ConstantInt, value.getValueKind())
        assertTrue { value.isNull() }
    }

    @Test
    fun `null pointer creation`() {
        val type = IntType(32)
        val nullptr = type.getConstantNullPointer()

        assertEquals(ValueKind.ConstantPointerNull, nullptr.getValueKind())
        assertTrue { nullptr.isNull() }
    }

    @Test
    fun `creation of undefined type object`() {
        val type = IntType(1032)
        val undef = type.getConstantUndef()

        assertEquals(ValueKind.UndefValue, undef.getValueKind())
        assertTrue { undef.isUndef() }
    }

    @Test
    fun `value type matches`() {
        val type = IntType(32)
        val value = ConstantInt(type, 1L, true)

        val valueType = value.getType()

        assertEquals(type.getTypeKind(), valueType.getTypeKind())
        assertEquals(type.getTypeWidth(), valueType.asIntType().getTypeWidth())
        assertTrue { value.isConstant() }
    }

    @Test
    fun `isa checks match`() {
        val type = IntType(32)
        val value = ConstantInt(type, 1L, true)

        assertFalse { value.isMetadataNode() }
        assertFalse { value.isMetadataString() }
    }
}
