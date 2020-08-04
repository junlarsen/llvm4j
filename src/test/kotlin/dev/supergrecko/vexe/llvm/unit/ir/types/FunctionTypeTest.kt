package dev.supergrecko.vexe.llvm.unit.ir.types

import dev.supergrecko.vexe.llvm.ir.TypeKind
import dev.supergrecko.vexe.llvm.ir.types.FloatType
import dev.supergrecko.vexe.llvm.ir.types.FunctionType
import dev.supergrecko.vexe.llvm.ir.types.IntType
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.bytedeco.llvm.global.LLVM
import org.spekframework.spek2.Spek

internal class FunctionTypeTest : Spek({
    test("create parameter-less function type") {
        val ret = IntType(64)
        val fn = FunctionType(ret, listOf(), true)

        assertEquals(fn.getParameterCount(), 0)
        assertTrue { fn.getReturnType().ref == ret.ref }
    }

    test("create of variadic function type") {
        val ret = IntType(64)
        val args = listOf(FloatType(TypeKind.Float))
        val fn = FunctionType(ret, args, true)

        assertTrue { fn.isVariadic() }
    }

    test("the given parameter size matches") {
        val ret = IntType(64)
        val args = listOf(FloatType(TypeKind.Float))
        val fn = FunctionType(ret, args, true)

        assertEquals(LLVM.LLVMCountParamTypes(fn.ref), fn.getParameterCount())
    }

    test("the passed parameters match") {
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

    test("the given return type matches") {
        val ret = IntType(64)
        val args = listOf(FloatType(TypeKind.Float))
        val fn = FunctionType(ret, args, true)

        val returns = fn.getReturnType()

        assertEquals(ret.ref, returns.ref)
    }
})
