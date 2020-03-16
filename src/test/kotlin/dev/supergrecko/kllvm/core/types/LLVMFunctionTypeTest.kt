package dev.supergrecko.kllvm.core.types

import dev.supergrecko.kllvm.core.LLVMType
import dev.supergrecko.kllvm.utils.toBoolean
import org.bytedeco.llvm.global.LLVM
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LLVMFunctionTypeTest {
    @Test
    fun `creation of zero arg type works`() {
        val ret = LLVMType.makeInteger(LLVMTypeKind.Integer.LLVM_I64_TYPE)
        val fn = LLVMType.makeFunction(ret, listOf(), false)

        assertEquals(fn.getParameterCount(), 0)
        assertTrue { fn.getReturnType().llvmType == ret.llvmType }
    }

    @Test
    fun `variadic arguments work`() {
        val ret = LLVMType.makeInteger(LLVMTypeKind.Integer.LLVM_I64_TYPE)
        val arg = LLVMType.make(LLVMTypeKind.LLVM_FLOAT_TYPE)
        val fn = LLVMType.makeFunction(ret, listOf(arg), true)

        assertEquals(fn.isVariadic(), true)
    }

    @Test
    fun `test variadic wrapper works`() {
        val ret = LLVMType.makeInteger(LLVMTypeKind.Integer.LLVM_I64_TYPE)
        val arg = LLVMType.make(LLVMTypeKind.LLVM_FLOAT_TYPE)
        val fn = LLVMType.makeFunction(ret, listOf(arg), true)

        assertEquals(LLVM.LLVMIsFunctionVarArg(fn.llvmType).toBoolean(), fn.isVariadic())
    }

    @Test
    fun `test parameter count wrapper works`() {
        val ret = LLVMType.makeInteger(LLVMTypeKind.Integer.LLVM_I64_TYPE)
        val arg = LLVMType.make(LLVMTypeKind.LLVM_FLOAT_TYPE)
        val fn = LLVMType.makeFunction(ret, listOf(arg), true)

        assertEquals(LLVM.LLVMCountParamTypes(fn.llvmType), fn.getParameterCount())
    }

    @Test
    fun `test parameter list matches`() {
        val ret = LLVMType.makeInteger(LLVMTypeKind.Integer.LLVM_I64_TYPE)
        val args = listOf(LLVMType.make(LLVMTypeKind.LLVM_FLOAT_TYPE))
        val fn = LLVMType.makeFunction(ret, args, true)

        val params = fn.getParameterTypes()

        for (i in args.indices) {
            val x = params[i].llvmType
            val y = params[i].llvmType
            assertEquals(x, y)
        }
    }
}