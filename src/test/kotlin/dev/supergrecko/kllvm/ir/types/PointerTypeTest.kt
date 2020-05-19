package dev.supergrecko.kllvm.ir.types

import dev.supergrecko.kllvm.ir.TypeKind
import kotlin.test.assertEquals
import org.junit.jupiter.api.Test

class PointerTypeTest {
    @Test
    fun `Creation from user-land constructor`() {
        val type = IntType(64)
        val ptr = type.toPointerType()

        assertEquals(TypeKind.Pointer, ptr.getTypeKind())
    }

    @Test
    fun `Creation via LLVM reference`() {
        val type = IntType(1).toPointerType(10)
        val second = PointerType(type.ref)

        assertEquals(TypeKind.Pointer, second.getTypeKind())
    }

    @Test
    fun `The underlying type matches`() {
        val type = IntType(32)
        val ptr = type.toPointerType()

        assertEquals(type.ref, ptr.getElementType().ref)
    }

    @Test
    fun `The element subtype matches`() {
        val type = IntType(32)
        val ptr = type.toPointerType()

        assertEquals(type.getTypeKind(), ptr.getSubtypes().first().getTypeKind())
    }

    @Test
    fun `The element count is 1 for pointers`() {
        val type = IntType(32).toPointerType()

        assertEquals(1, type.getElementCount())
    }

    @Test
    fun `A given address space matches`() {
        val type = IntType(32)
        val ptr = type.toPointerType(100)

        assertEquals(100, ptr.getAddressSpace())
    }
}
