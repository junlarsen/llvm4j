package dev.supergrecko.kllvm.core.types

import dev.supergrecko.kllvm.factories.TypeFactory
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class LLVMStructureTypeTest {
    @Test
    fun `test element spec matches`() {
        val elements = listOf(TypeFactory.integer(32))
        val struct = TypeFactory.struct(elements, false)

        assertEquals(false, struct.isPackedStruct())
        assertEquals(1, struct.getElementSize())
        assertEquals(true, struct.isLiteralStruct())
        assertEquals(false, struct.isOpaqueStruct())

        val (first) = struct.getStructElementTypes()
        assertEquals(elements.first().llvmType, first.llvmType)

        val type = struct.getElementTypeAt(0)
        assertEquals(type.llvmType, elements.first().llvmType)
    }

    @Test
    fun `test opaque struct`() {
        val struct = TypeFactory.opaque("test_struct")

        assertEquals(true, struct.isOpaqueStruct())

        val elements = listOf(TypeFactory.integer(32))
        struct.setStructBody(elements, false)

        val (first) = struct.getStructElementTypes()
        assertEquals(elements.first().llvmType, first.llvmType)
        assertEquals(false, struct.isOpaqueStruct())
    }
}
