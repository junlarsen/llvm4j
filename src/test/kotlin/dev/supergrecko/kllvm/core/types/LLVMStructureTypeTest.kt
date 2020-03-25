package dev.supergrecko.kllvm.core.types

import dev.supergrecko.kllvm.factories.TypeFactory
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class LLVMStructureTypeTest {
    @Test
    fun `test element spec matches`() {
        val elements = listOf(TypeFactory.integer(32))
        val struct = TypeFactory.struct(elements, false)

        assertEquals(false, struct.isPacked())
        assertEquals(1, struct.getElementCount())
        assertEquals(true, struct.isLiteral())
        assertEquals(false, struct.isOpaque())

        val (first) = struct.getElementTypes()
        assertEquals(elements.first().llvmType, first.llvmType)

        val type = struct.getElementTypeAt(0)
        assertEquals(type.llvmType, elements.first().llvmType)
    }

    @Test
    fun `test opaque struct`() {
        val struct = TypeFactory.opaque("test_struct")

        assertEquals(true, struct.isOpaque())

        val elements = listOf(TypeFactory.integer(32))
        struct.setBody(elements, false)

        val (first) = struct.getElementTypes()
        assertEquals(elements.first().llvmType, first.llvmType)
        assertEquals(false, struct.isOpaque())
    }
}
