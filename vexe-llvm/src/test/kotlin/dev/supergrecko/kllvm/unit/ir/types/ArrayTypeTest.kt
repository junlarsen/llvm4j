package dev.supergrecko.kllvm.unit.ir.types

import dev.supergrecko.kllvm.ir.TypeKind
import dev.supergrecko.kllvm.ir.types.ArrayType
import dev.supergrecko.kllvm.ir.types.IntType
import dev.supergrecko.kllvm.utils.KLLVMTestCase
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import org.junit.jupiter.api.Test

internal class ArrayTypeTest : KLLVMTestCase() {
    @Test
    fun `Creation from user-land constructor`() {
        val type = IntType(64)
        val arr = type.toArrayType(10)

        assertEquals(TypeKind.Array, arr.getTypeKind())
        assertEquals(10, arr.getElementCount())
    }

    @Test
    fun `Creation via LLVM reference`() {
        val type = IntType(1).toArrayType(10)
        val second = ArrayType(type.ref)

        assertEquals(TypeKind.Array, second.getTypeKind())
    }

    @Test
    fun `The LLVMType references match each other`() {
        val type = IntType(32)
        val arr = ArrayType(type, 10)

        assertEquals(10, arr.getElementCount())
        assertEquals(type.ref, arr.getElementType().ref)
    }

    @Test
    fun `The Subtype trait refers to the same LLVMType`() {
        val type = IntType(32)
        val arr = ArrayType(type, 10)

        val children = arr.getSubtypes()

        assertEquals(10, children.size)
        assertEquals(type.ref, children.first().ref)
    }

    @Test
    fun `Declaring type of negative size fails`() {
        val type = IntType(32)

        assertFailsWith<IllegalArgumentException> {
            type.toArrayType(-100)
        }
    }
}
