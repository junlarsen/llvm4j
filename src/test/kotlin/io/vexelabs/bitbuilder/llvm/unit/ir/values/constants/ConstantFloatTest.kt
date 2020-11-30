package io.vexelabs.bitbuilder.llvm.unit.ir.values.constants

import io.vexelabs.bitbuilder.llvm.ir.Context
import io.vexelabs.bitbuilder.llvm.ir.RealPredicate
import io.vexelabs.bitbuilder.llvm.ir.TypeKind
import io.vexelabs.bitbuilder.llvm.ir.types.FloatType
import io.vexelabs.bitbuilder.llvm.setup
import org.spekframework.spek2.Spek
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal object ConstantFloatTest : Spek({
    setup()

    val context: Context by memoized()

    val f64 by memoized { context.getFloatType(TypeKind.Double) }
    val rhs by memoized { f64.getConstant(100.0) }
    val lhs by memoized { f64.getConstant(100.0) }

    test("create from double value") {
        assertEquals(100.0, lhs.getDouble())
        assertEquals(100.0, rhs.getDouble())
    }

    test("creating a large double will lose precision") {
        val f128 = context.getFloatType(TypeKind.FP128)
        val v1 = f128.getConstant(Double.MAX_VALUE)
        val v2 = f128.getConstant(Double.MAX_VALUE)
        val value = v1.getAdd(v2)

        assertTrue { value.getDoubleLosesPrecision() }
        assertEquals(Double.POSITIVE_INFINITY, value.getDouble())
    }

    test("get the negation value") {
        assertEquals(-100.0, rhs.getNeg().getDouble())
    }

    test("get the sum of two floats") {
        val sum = lhs.getAdd(rhs)

        assertEquals(200.0, sum.getDouble())
    }

    test("get the difference between two floats") {
        val diff = lhs.getSub(rhs)

        assertEquals(0.0, diff.getDouble())
    }

    test("get the product of two floats") {
        val product = lhs.getMul(rhs)

        assertEquals(10000.0, product.getDouble())
    }

    test("get the quotient of two floats") {
        val quotient = lhs.getDiv(rhs)

        assertEquals(1.0, quotient.getDouble())
    }

    test("get the remainder of this and another float") {
        val rem = lhs.getRem(rhs)

        assertEquals(0.0, rem.getDouble())
    }

    test("perform comparison of two floats") {
        val left = f64.getConstant(10.9)
        val right = f64.getConstant(9.1)

        val expected = arrayOf<Long>(
            0, 0, // false, oeq
            1, 1, // ogt, oge
            0, 0, // olt, ole
            1, 1, // one, ord
            0, 0, // ueq, ugt
            1, 1, // uge, ult
            0, 0, // ule, une
            1, 1 // uno, true
        )

        for ((idx, it) in RealPredicate.values().withIndex()) {
            val res = left.getFCmp(it, right).getUnsignedValue()
            val expect = expected[idx]

            assertEquals(expect, res)
        }
    }

    test("truncating to a tinier float type") {
        val left = f64.getConstant(Double.MAX_VALUE)
        val f32 = context.getFloatType(TypeKind.Float)
        // Double: f64, Float: f32
        val trunc = left.getTrunc(f32)

        // See https://llvm.org/docs/LangRef.html#id252
        assertEquals(Float.POSITIVE_INFINITY, trunc.getDouble().toFloat())
        assertEquals(TypeKind.Float, trunc.getType().getTypeKind())
    }

    test("extending to a larger float type") {
        val f128 = context.getFloatType(TypeKind.FP128)
        val ext = lhs.getExt(f128)

        assertEquals(100.0, ext.getDouble())
        assertEquals(TypeKind.FP128, ext.getType().getTypeKind())
    }

    test("casting into integer type") {
        val i32 = context.getIntType(32)
        // 64-bit to 32-bit
        val ui = lhs.getFPToUI(i32)
        val si = lhs.getFPToSI(i32)

        assertEquals(100, si.getSignedValue())
        assertEquals(100, ui.getSignedValue())
        assertEquals(TypeKind.Integer, ui.getType().getTypeKind())
        assertEquals(TypeKind.Integer, si.getType().getTypeKind())
    }

    test("cast into any other floating point type") {
        // TODO: BFloat16?
        val kinds = FloatType.kinds.toMutableList().also {
            it.remove(TypeKind.BFloat)
        }

        for (it in kinds) {
            val fx = context.getFloatType(it)
            val cast = lhs.getFPCast(fx)

            assertEquals(it, cast.getType().getTypeKind())
        }
    }
})
