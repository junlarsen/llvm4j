package dev.supergrecko.kllvm.core.types

import dev.supergrecko.kllvm.core.LLVMType
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class LLVMStructureTypeTest {
    @Test
    fun `test element spec matches`() {
        val elements = listOf(LLVMType.createInteger(32))
        val struct = LLVMType.createStruct(elements, false)

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
        val struct = LLVMType.createStruct(listOf(), false, "test_struct")

        assertEquals(true, struct.isOpaqueStruct())

        val elements = listOf(LLVMType.createInteger(32))
        struct.setStructBody(elements, false)

        val (first) = struct.getStructElementTypes()
        assertEquals(elements.first().llvmType, first.llvmType)
        assertEquals(false, struct.isOpaqueStruct())
    }
}
