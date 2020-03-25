package dev.supergrecko.kllvm.core.types

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class StructureTypeTest {
    @Test
    fun `test element spec matches`() {
        val elements = listOf(IntType.new(32))
        val struct = StructType.new(elements, false)

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
    fun `name matches`() {
        val struct = StructType.opaque("StructureName")

        assertEquals("StructureName", struct.getName())
    }

    @Test
    fun `test opaque struct`() {
        val struct = StructType.opaque("test_struct")

        assertEquals(true, struct.isOpaque())

        val elements = listOf(IntType.new(32))
        struct.setBody(elements, false)

        val (first) = struct.getElementTypes()
        assertEquals(elements.first().llvmType, first.llvmType)
        assertEquals(false, struct.isOpaque())
    }
}
