package dev.supergrecko.kllvm.core.type

import dev.supergrecko.kllvm.core.LLVMContext
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class LLVMStructureTypeTest {
    @Test
    fun `test element spec matches`() {
        val elements = listOf(LLVMType.makeInteger(LLVMTypeKind.Integer.LLVM_I32_TYPE))
        val struct = LLVMType.makeStruct(elements, false)

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
        val ctx = LLVMContext.create()
        val struct = LLVMType.makeStruct(listOf(), false, "test_struct")

        assertEquals(true, struct.isOpaque())

        val elements = listOf(LLVMType.makeInteger(LLVMTypeKind.Integer.LLVM_I32_TYPE))
        struct.setBody(elements, false)

        val (first) = struct.getElementTypes()
        assertEquals(elements.first().llvmType, first.llvmType)
        assertEquals(false, struct.isOpaque())
    }
}