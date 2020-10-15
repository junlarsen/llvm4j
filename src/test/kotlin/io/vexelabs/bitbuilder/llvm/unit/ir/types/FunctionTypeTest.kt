package io.vexelabs.bitbuilder.llvm.unit.ir.types

import io.vexelabs.bitbuilder.llvm.ir.TypeKind
import io.vexelabs.bitbuilder.llvm.ir.types.FloatType
import io.vexelabs.bitbuilder.llvm.ir.types.FunctionType
import io.vexelabs.bitbuilder.llvm.ir.types.IntType
import org.spekframework.spek2.Spek
import kotlin.test.assertEquals
import kotlin.test.assertTrue

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

        assertEquals(1, fn.getParameterCount())
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
