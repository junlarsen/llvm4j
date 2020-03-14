package dev.supergrecko.kllvm.core.type

import org.bytedeco.llvm.global.LLVM
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class LLVMTypeTest {
    @Test
    fun `test creation of pointer type`() {
        val type = LLVMType.makeInteger(LLVMTypeKind.Integer.LLVM_I64_TYPE)

        val ptr = type.asPointer()

        assertEquals(LLVM.LLVMGetTypeKind(ptr), LLVM.LLVMPointerTypeKind)
    }
}
