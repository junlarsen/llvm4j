package io.vexelabs.bitbuilder.llvm.unit.ir.types

import io.vexelabs.bitbuilder.llvm.ir.Context
import io.vexelabs.bitbuilder.llvm.ir.TypeKind
import io.vexelabs.bitbuilder.llvm.ir.types.FloatType
import io.vexelabs.bitbuilder.llvm.ir.types.FunctionType
import io.vexelabs.bitbuilder.llvm.ir.types.IntType
import io.vexelabs.bitbuilder.llvm.setup
import org.spekframework.spek2.Spek
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class FunctionTypeTest : Spek({
    setup()

    val context: Context by memoized()

    test("create parameter-less function type") {
        val ret = context.getIntType(32)
        val fn = context.getFunctionType(ret, variadic = false)

        assertEquals(fn.getParameterCount(), 0)
        assertTrue { fn.getReturnType().ref == ret.ref }
    }

    test("create of variadic function type") {
        val ret = context.getIntType(32)
        val fn = context.getFunctionType(ret, variadic = true)

        assertTrue { fn.isVariadic() }
    }

    test("the given parameter size matches") {
        val ret = context.getIntType(32)
        val args = context.getFloatType(TypeKind.Float)
        val fn = context.getFunctionType(ret, args, variadic = true)

        assertEquals(1, fn.getParameterCount())
    }

    test("the passed parameters match") {
        val ret = context.getIntType(32)
        val args = context.getFloatType(TypeKind.Float)
        val fn = context.getFunctionType(ret, args, variadic = true)
        val params = fn.getParameterTypes()

        assertEquals(args.ref, params[0].ref)
    }

    test("the given return type matches") {
        val ret = context.getIntType(32)
        val args = context.getFloatType(TypeKind.Float)
        val fn = context.getFunctionType(ret, args, variadic = false)

        val returns = fn.getReturnType()

        assertEquals(ret.ref, returns.ref)
    }
})
