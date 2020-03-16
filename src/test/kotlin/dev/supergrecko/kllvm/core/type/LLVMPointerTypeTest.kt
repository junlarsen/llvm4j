package dev.supergrecko.kllvm.core.type

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class LLVMPointerTypeTest {
    @Test
    fun `underlying type matches`() {
        val type = LLVMType.makeInteger(LLVMTypeKind.Integer.LLVM_I32_TYPE)
        val ptr = type.asPointer()

        assertEquals(type.llvmType, ptr.getElementType().llvmType)
    }

    @Test
    fun `address space matches`() {
        val type = LLVMType.makeInteger(LLVMTypeKind.Integer.LLVM_I32_TYPE)
        val ptr = type.asPointer(100)

        assertEquals(100, ptr.getAddressSpace())
    }
}