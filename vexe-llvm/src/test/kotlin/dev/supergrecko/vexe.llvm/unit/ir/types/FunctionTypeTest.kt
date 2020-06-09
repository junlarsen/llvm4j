package dev.supergrecko.vexe.llvm.unit.ir.types

import dev.supergrecko.vexe.llvm.ir.TypeKind
import dev.supergrecko.vexe.llvm.ir.types.FloatType
import dev.supergrecko.vexe.llvm.ir.types.FunctionType
import dev.supergrecko.vexe.llvm.ir.types.IntType
import dev.supergrecko.vexe.llvm.utils.KLLVMTestCase
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.bytedeco.llvm.global.LLVM
import org.junit.jupiter.api.Test

internal class FunctionTypeTest : KLLVMTestCase() {
    @Test
    fun `Creation of parameter-less function`() {
        val ret = IntType(64)
        val fn = FunctionType(ret, listOf(), true)

        assertEquals(fn.getParameterCount(), 0)
        assertTrue { fn.getReturnType().ref == ret.ref }
    }

    @Test
    fun `Creation of variadic function`() {
        val ret = IntType(64)
        val args = listOf(FloatType(TypeKind.Float))
        val fn = FunctionType(ret, args, true)

        assertTrue { fn.isVariadic() }
    }

    @Test
    fun `Parameter size matches`() {
        val ret = IntType(64)
        val args = listOf(FloatType(TypeKind.Float))
        val fn = FunctionType(ret, args, true)

        assertEquals(LLVM.LLVMCountParamTypes(fn.ref), fn.getParameterCount())
    }

    @Test
    fun `List of parameters match`() {
        val ret = IntType(64)
        val args = listOf(FloatType(TypeKind.Float))
        val fn = FunctionType(ret, args, true)

        val params = fn.getParameterTypes()

        for (i in args.indices) {
            val x = args[i].ref
            val y = params[i].ref
            assertEquals(x, y)
        }
    }

    @Test
    fun `Return type matches`() {
        val ret = IntType(64)
        val args = listOf(FloatType(TypeKind.Float))
        val fn = FunctionType(ret, args, true)

        val returns = fn.getReturnType()

        assertEquals(ret.ref, returns.ref)
    }
}
