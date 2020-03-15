package dev.supergrecko.kllvm.core.type

import dev.supergrecko.kllvm.utils.runAll
import org.bytedeco.llvm.global.LLVM
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LLVMTypeTest {
    @Test
    fun `test creation of pointer type`() {
        val type = LLVMType.makeInteger(LLVMTypeKind.Integer.LLVM_I64_TYPE)

        val ptr = type.asPointer()

        assertEquals(LLVM.LLVMGetTypeKind(ptr), LLVM.LLVMPointerTypeKind)
    }

    @Test
    fun `creation of each type`() {
        runAll(*LLVMTypeKind.values()) {
            val type = LLVMType.make(it)
            assertTrue { !type.llvmType.isNull }
        }
    }
}
