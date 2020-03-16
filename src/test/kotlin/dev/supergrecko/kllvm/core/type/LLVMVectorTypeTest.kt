package dev.supergrecko.kllvm.core.type

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class LLVMVectorTypeTest {
    @Test
    fun `underlying type matches`() {
        val type = LLVMType.makeInteger(LLVMTypeKind.Integer.LLVM_I32_TYPE)
        val vec = LLVMType.makeVector(type, 10)

        assertEquals(10, vec.getSize())
        assertEquals(type.llvmType, vec.getElementType().llvmType)
    }
}