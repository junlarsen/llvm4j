package dev.supergrecko.kllvm.core.types

import dev.supergrecko.kllvm.factories.TypeFactory
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class PointerTypeTest {
    @Test
    fun `underlying type matches`() {
        val type = TypeFactory.integer(32)
        val ptr = type.toPointerType()

        assertEquals(type.llvmType, ptr.getElementType().llvmType)
    }

    @Test
    fun `subtype matches`() {
        val type = TypeFactory.integer(32)
        val ptr = type.toPointerType()

        assertEquals(type.getTypeKind(), ptr.getSubtypes().first().getTypeKind())
    }

    @Test
    fun `count matches`() {
        val type = TypeFactory.integer(32).toPointerType()

        assertEquals(1, type.getElementCount())
    }

    @Test
    fun `address space matches`() {
        val type = TypeFactory.integer(32)
        val ptr = type.toPointerType(100)

        assertEquals(100, ptr.getAddressSpace())
    }
}