package dev.supergrecko.kllvm.core.types

import dev.supergrecko.kllvm.core.LLVMType
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class LLVMStructureTypeTest {
    @Test
    fun `test element spec matches`() {
        val elements = listOf(LLVMType.makeInteger(32))
        val struct = LLVMType.createStruct(elements, false)

        assertEquals(false, struct.isPacked())
        assertEquals(1, struct.getElementTypeCount())
        assertEquals(true, struct.isLiteral())
        assertEquals(false, struct.isOpaque())

        val (first) = struct.getElementTypes()
        assertEquals(elements.first().llvmType, first.llvmType)

        val type = struct.getType(0)
        assertEquals(type.llvmType, elements.first().llvmType)
    }

    @Test
    fun `test opaque struct`() {
        val struct = LLVMType.createStruct(listOf(), false, "test_struct")

        assertEquals(true, struct.isOpaque())

        val elements = listOf(LLVMType.makeInteger(32))
        struct.setBody(elements, false)

        val (first) = struct.getElementTypes()
        assertEquals(elements.first().llvmType, first.llvmType)
        assertEquals(false, struct.isOpaque())
    }
}