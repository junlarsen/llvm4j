package dev.supergrecko.kllvm.core.type

import dev.supergrecko.kllvm.utils.toBoolean
import org.bytedeco.llvm.global.LLVM
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class LLVMFunctionTypeTest {
    @Test
    fun `creation of zero arg type works`() {
        val ret = LLVMIntegerType.type(64)

        val fn = LLVMFunctionType.type(ret, listOf(), false)

        assertEquals(fn.getParameterCount(), 0)
        assertEquals(fn.getReturnType().llvmType, ret.llvmType)
    }

    @Test
    fun `variadic arguments work`() {
        val ret = LLVMIntegerType.type(64)
        val arg = LLVMFloatType.type(LLVMType.FloatTypeKinds.LLVM_FLOAT_TYPE)

        val fn = LLVMFunctionType.type(ret, listOf(arg), true)

        assertEquals(fn.isVariadic(), true)
    }

    @Test
    fun `test variadic wrapper works`() {
        val ret = LLVMIntegerType.type(64)
        val arg = LLVMFloatType.type(LLVMType.FloatTypeKinds.LLVM_FLOAT_TYPE)

        val fn = LLVMFunctionType.type(ret, listOf(arg), true)

        assertEquals(LLVM.LLVMIsFunctionVarArg(fn.llvmType).toBoolean(), fn.isVariadic())
    }

    @Test
    fun `test parameter count wrapper works`() {
        val ret = LLVMIntegerType.type(64)
        val arg = LLVMFloatType.type(LLVMType.FloatTypeKinds.LLVM_FLOAT_TYPE)

        val fn = LLVMFunctionType.type(ret, listOf(arg), true)

        assertEquals(LLVM.LLVMCountParamTypes(fn.llvmType), fn.getParameterCount())
    }
}