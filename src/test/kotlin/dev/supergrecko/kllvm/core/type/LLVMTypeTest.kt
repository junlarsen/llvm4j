package dev.supergrecko.kllvm.core.type

import org.bytedeco.llvm.global.LLVM
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class LLVMTypeTest {
    @Test
    fun `test creation of pointer type`() {
        val type = LLVMIntegerType.type(64)

        val ptr = type.asPointer()

        assertEquals(LLVM.LLVMGetTypeKind(ptr), LLVM.LLVMPointerTypeKind)
    }
}
