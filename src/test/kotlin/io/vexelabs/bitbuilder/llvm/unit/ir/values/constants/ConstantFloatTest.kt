package io.vexelabs.bitbuilder.llvm.unit.ir.values.constants

import io.vexelabs.bitbuilder.llvm.ir.RealPredicate
import io.vexelabs.bitbuilder.llvm.ir.TypeKind
import io.vexelabs.bitbuilder.llvm.ir.types.FloatType
import io.vexelabs.bitbuilder.llvm.ir.types.IntType
import io.vexelabs.bitbuilder.llvm.ir.values.constants.ConstantFloat
import io.vexelabs.bitbuilder.llvm.setup
import org.spekframework.spek2.Spek
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal object ConstantFloatTest : Spek({
    setup()

    val double by memoized { FloatType(TypeKind.Double) }
    val rhs by memoized { ConstantFloat(double, 100.0) }
    val lhs by memoized { ConstantFloat(double, 100.0) }

    test("create from double value") {
        val float = ConstantFloat(FloatType(TypeKind.Double), 100.0)

        assertEquals(100.0, float.getDouble())
    }

    test("creating a large double will lose precision") {
        val ty = FloatType(TypeKind.FP128)
        val v1 = ConstantFloat(ty, Double.MAX_VALUE)
        val v2 = ConstantFloat(ty, Double.MAX_VALUE)
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
        val left = ConstantFloat(double, 10.9)
        val right = ConstantFloat(double, 9.1)

        val expected = arrayOf<Long>(
            0, 0, // false, oeq
            1, 1, // ogt, oge
            0, 0, // olt, ole
            1, 1, // one, ord
            0, 0, // ueq, ugt
            1, 1, // uge, ult
            0, 0, // ule, une
            1, 1  // uno, true
        )

        for ((idx, it) in RealPredicate.values().withIndex()) {
            val res = left.getFCmp(it, right).getUnsignedValue()
            val expect = expected[idx]

            assertEquals(expect, res)
        }
    }

    test("truncating to a tinier float type") {
        val left = ConstantFloat(double, Double.MAX_VALUE)
        // Double: f64, Float: f32
        val trunc = left.getTrunc(FloatType(TypeKind.Float))

        // See https://llvm.org/docs/LangRef.html#id252
        assertEquals(Float.POSITIVE_INFINITY, trunc.getDouble().toFloat())
        assertEquals(TypeKind.Float, trunc.getType().getTypeKind())
    }

    test("extending to a larger float type") {
        val ext = lhs.getExt(FloatType(TypeKind.FP128))

        assertEquals(100.0, ext.getDouble())
        assertEquals(TypeKind.FP128, ext.getType().getTypeKind())
    }

    test("casting into integer type") {
        // 64-bit to 32-bit
        val ui = lhs.getFPToUI(IntType(32))
        val si = lhs.getFPToSI(IntType(32))

        assertEquals(100, si.getSignedValue())
        assertEquals(100, ui.getSignedValue())
        assertEquals(TypeKind.Integer, ui.getType().getTypeKind())
        assertEquals(TypeKind.Integer, si.getType().getTypeKind())
    }

    test("cast into any other floating point type") {
        for (it in FloatType.kinds) {
            val cast = lhs.getFPCast(FloatType(it))

            assertEquals(it, cast.getType().getTypeKind())
        }
    }
})
