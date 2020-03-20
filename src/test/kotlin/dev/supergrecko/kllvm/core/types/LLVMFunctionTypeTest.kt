package dev.supergrecko.kllvm.core.types

import dev.supergrecko.kllvm.core.enumerations.LLVMTypeKind
import dev.supergrecko.kllvm.factories.TypeFactory
import dev.supergrecko.kllvm.utils.toBoolean
import org.bytedeco.llvm.global.LLVM
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LLVMFunctionTypeTest {
    @Test
    fun `creation of zero arg type works`() {
        val ret = TypeFactory.integer(64)
        val fn = TypeFactory.function(ret, listOf(), true)

        assertEquals(fn.getFunctionParameterCount(), 0)
        assertTrue { fn.getFunctionReturnType().llvmType == ret.llvmType }
    }

    @Test
    fun `variadic arguments work`() {
        val ret = TypeFactory.integer(64)
        val args = listOf(TypeFactory.float(LLVMTypeKind.Float))
        val fn = TypeFactory.function(ret, args, true)

        assertEquals(fn.isFunctionVariadic(), true)
    }

    @Test
    fun `test variadic wrapper works`() {
        val ret = TypeFactory.integer(64)
        val args = listOf(TypeFactory.float(LLVMTypeKind.Float))
        val fn = TypeFactory.function(ret, args, true)

        assertEquals(LLVM.LLVMIsFunctionVarArg(fn.llvmType).toBoolean(), fn.isFunctionVariadic())
    }

    @Test
    fun `test parameter count wrapper works`() {
        val ret = TypeFactory.integer(64)
        val args = listOf(TypeFactory.float(LLVMTypeKind.Float))
        val fn = TypeFactory.function(ret, args, true)

        assertEquals(LLVM.LLVMCountParamTypes(fn.llvmType), fn.getFunctionParameterCount())
    }

    @Test
    fun `test parameter list matches`() {
        val ret = TypeFactory.integer(32)
        val args = listOf(TypeFactory.float(LLVMTypeKind.Float))
        val fn = TypeFactory.function(ret, args, true)

        val params = fn.getFunctionParameterTypes()

        for (i in args.indices) {
            val x = params[i].llvmType
            val y = params[i].llvmType
            assertEquals(x, y)
        }
    }
}
