package dev.supergrecko.kllvm.ir.types

import dev.supergrecko.kllvm.ir.TypeKind
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import org.junit.jupiter.api.Test

class ArrayTypeTest {
    @Test
    fun `user land creation of type`() {
        val type = IntType(64)
        val arr = type.toArrayType(10)

        assertEquals(TypeKind.Array, arr.getTypeKind())
        assertEquals(10, arr.getElementCount())
    }

    @Test
    fun `ref creation of type`() {
        val type = IntType(1).toArrayType(10)
        val second = ArrayType(type.ref)

        assertEquals(TypeKind.Array, second.getTypeKind())
    }

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
