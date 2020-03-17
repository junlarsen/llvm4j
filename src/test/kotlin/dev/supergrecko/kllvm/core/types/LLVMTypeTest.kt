package dev.supergrecko.kllvm.core.types

import dev.supergrecko.kllvm.core.LLVMType
import dev.supergrecko.kllvm.core.enumerations.LLVMTypeKind
import org.bytedeco.llvm.global.LLVM
import org.junit.jupiter.api.Test
import kotlin.test.*

class LLVMTypeTest {
    @Test
    fun `test creation of pointer type`() {
        val type = LLVMType.createInteger(64)

        val ptr = type.toPointer()

        assertEquals(LLVM.LLVMGetTypeKind(ptr.llvmType), LLVM.LLVMPointerTypeKind)
    }

    @Test
    fun `casting into other type works when expected to`() {
        val type = LLVMType.createInteger(32)
        val ptr = type.toPointer()
        val underlying = ptr.getElementType()

        assertEquals(type.llvmType, underlying.cast(LLVMTypeKind.Integer).llvmType)
    }

    @Test
    fun `casting won't fail when the underlying type is different`() {
        // This behavior is documented at LLVMType. There is no way
        // to guarantee that the underlying types is valid or invalid
        val type = LLVMType.createInteger(32)
        val ptr = type.toPointer()
        val underlying = ptr.getElementType()

        assertEquals(type.llvmType, underlying.cast(LLVMTypeKind.Function).llvmType)
    }

    @Test
    fun `getting a type works properly`() {
        val type = LLVMType.create(LLVMTypeKind.Float)

        assertEquals(LLVMTypeKind.Float, type.getTypeKind())
    }
}
