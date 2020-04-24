package dev.supergrecko.kllvm.ir.types

import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import org.junit.jupiter.api.Test

class ArrayTypeTest {
    @Test
    fun `underlying type matches`() {
        val type = IntType(32)
        val arr = ArrayType(type, 10)

        assertEquals(10, arr.getElementCount())
        assertEquals(type.ref, arr.getElementType().ref)
    }

    @Test
    fun `subtypes match`() {
        val type = IntType(32)
        val arr = ArrayType(type, 10)

        val children = arr.getSubtypes()

        assertEquals(10, children.size)
        assertEquals(type.ref, children.first().ref)
    }

    @Test
    fun `negative size is illegal`() {
        val type = IntType(32)

        assertFailsWith<IllegalArgumentException> {
            type.toArrayType(-100)
        }
    }
}
