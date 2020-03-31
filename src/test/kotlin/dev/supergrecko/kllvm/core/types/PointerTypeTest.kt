package dev.supergrecko.kllvm.core.types

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class PointerTypeTest {
    @Test
    fun `underlying type matches`() {
        val type = IntType.new(32)
        val ptr = type.toPointerType()

        assertEquals(type.ref, ptr.getElementType().ref)
    }

    @Test
    fun `subtype matches`() {
        val type = IntType.new(32)
        val ptr = type.toPointerType()

        assertEquals(type.getTypeKind(), ptr.getSubtypes().first().getTypeKind())
    }

    @Test
    fun `count matches`() {
        val type = IntType.new(32).toPointerType()

        assertEquals(1, type.getElementCount())
    }

    @Test
    fun `address space matches`() {
        val type = IntType.new(32)
        val ptr = type.toPointerType(100)

        assertEquals(100, ptr.getAddressSpace())
    }
}