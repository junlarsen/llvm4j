package dev.supergrecko.kllvm.core.types

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ArrayTypeTest {
    @Test
    fun `underlying type matches`() {
        val type = IntType.new(32)
        val arr = ArrayType.new(type, 10)

        assertEquals(10, arr.getElementCount())
        assertEquals(type.llvmType, arr.getElementType().llvmType)
    }

    @Test
    fun `subtypes match`() {
        val type = IntType.new(32)
        val arr = ArrayType.new(type, 10)

        val children = arr.getSubtypes()

        assertEquals(10, children.size)
        assertEquals(type.llvmType, children.first().llvmType)
    }

    @Test
    fun `negative size is illegal`() {
        val type = IntType.new(32)

        assertFailsWith<IllegalArgumentException> {
            type.toArrayType(-100)
        }
    }
}
