package dev.supergrecko.kllvm.core.type

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class LLVMArrayTypeTest {
    @Test
    fun `underlying type matches`() {
        val type = LLVMType.makeInteger(LLVMTypeKind.Integer.LLVM_I32_TYPE)
        val arr = LLVMType.makeArray(type, 10)

        assertEquals(10, arr.getLength())
        assertEquals(type.llvmType, arr.getElementType().llvmType)
    }
}