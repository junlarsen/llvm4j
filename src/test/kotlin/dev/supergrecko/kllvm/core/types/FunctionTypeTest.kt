package dev.supergrecko.kllvm.core.types

import dev.supergrecko.kllvm.core.enumerations.TypeKind
import dev.supergrecko.kllvm.utils.toBoolean
import org.bytedeco.llvm.global.LLVM
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FunctionTypeTest {
    @Test
    fun `creation of zero arg type works`() {
        val ret = IntType.new(64)
        val fn = FunctionType.new(ret, listOf(), true)

        assertEquals(fn.getParameterCount(), 0)
        assertTrue { fn.getReturnType().llvmType == ret.llvmType }
    }

    @Test
    fun `variadic arguments work`() {
        val ret = IntType.new(64)
        val args = listOf(FloatType.new(TypeKind.Float))
        val fn = FunctionType.new(ret, args, true)

        assertEquals(fn.isVariadic(), true)
    }

    @Test
    fun `test variadic wrapper works`() {
        val ret = IntType.new(64)
        val args = listOf(FloatType.new(TypeKind.Float))
        val fn = FunctionType.new(ret, args, true)

        assertEquals(LLVM.LLVMIsFunctionVarArg(fn.llvmType).toBoolean(), fn.isVariadic())
    }

    @Test
    fun `test parameter count wrapper works`() {
        val ret = IntType.new(64)
        val args = listOf(FloatType.new(TypeKind.Float))
        val fn = FunctionType.new(ret, args, true)

        assertEquals(LLVM.LLVMCountParamTypes(fn.llvmType), fn.getParameterCount())
    }

    @Test
    fun `test parameter list matches`() {
        val ret = IntType.new(64)
        val args = listOf(FloatType.new(TypeKind.Float))
        val fn = FunctionType.new(ret, args, true)

        val params = fn.getParameterTypes()

        for (i in args.indices) {
            val x = params[i].llvmType
            val y = params[i].llvmType
            assertEquals(x, y)
        }
    }
}
