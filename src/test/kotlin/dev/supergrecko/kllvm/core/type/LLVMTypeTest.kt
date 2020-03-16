package dev.supergrecko.kllvm.core.type

import dev.supergrecko.kllvm.utils.runAll
import org.bytedeco.llvm.global.LLVM
import org.junit.jupiter.api.Test
import kotlin.test.*

class LLVMTypeTest {
    @Test
    fun `test creation of pointer type`() {
        val type = LLVMType.makeInteger(LLVMTypeKind.Integer.LLVM_I64_TYPE)

        val ptr = type.asPointer()

        assertEquals(LLVM.LLVMGetTypeKind(ptr.llvmType), LLVM.LLVMPointerTypeKind)
    }

    @Test
    fun `creation of each type`() {
        runAll(*LLVMTypeKind.values()) {
            val type = LLVMType.make(it)
            assertTrue { !type.llvmType.isNull }
        }
    }

    @Test
    fun `casting into other type works when expected to`() {
        val type = LLVMType.makeInteger(LLVMTypeKind.Integer.LLVM_I32_TYPE)
        val ptr = type.asPointer()
        val underlying = ptr.getElementType()

        assertEquals(type.llvmType, underlying.asInteger().llvmType)
    }

    @Test
    fun `casting won't fail when the underlying type is different`() {
        // This behavior is documented at LLVMType. There is no way
        // to guarantee that the underlying type is valid or invalid
        val type = LLVMType.makeInteger(LLVMTypeKind.Integer.LLVM_I32_TYPE)
        val ptr = type.asPointer()
        val underlying = ptr.getElementType()

        assertEquals(type.llvmType, underlying.asFunction().llvmType)
    }
}