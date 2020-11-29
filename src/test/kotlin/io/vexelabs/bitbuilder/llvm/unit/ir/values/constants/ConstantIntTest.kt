package io.vexelabs.bitbuilder.llvm.unit.ir.values.constants

import io.vexelabs.bitbuilder.internal.cast
import io.vexelabs.bitbuilder.llvm.ir.Context
import io.vexelabs.bitbuilder.llvm.ir.IntPredicate
import io.vexelabs.bitbuilder.llvm.ir.TypeKind
import io.vexelabs.bitbuilder.llvm.ir.values.constants.ConstantInt
import io.vexelabs.bitbuilder.llvm.setup
import io.vexelabs.bitbuilder.llvm.utils.runAll
import org.spekframework.spek2.Spek
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class ConstantIntTest : Spek({
    setup()

    val context: Context by memoized()

    test("create integer from long words constructor") {
        val ty = context.getIntType(32)
        val value = ConstantInt(ty, listOf(100L, 20L))

        assertEquals(100, value.getSignedValue())
    }

    test("get the negation value") {
        val ty = context.getIntType(32)
        val v = ConstantInt(ty, 100)
        val neg = v.getNeg()

        assertEquals(-100L, neg.getSignedValue())
    }

    test("get the inversion value") {
        val ty = context.getIntType(32)
        val v = ConstantInt(ty, 100)
        val not = v.getNot()

        assertEquals(-101, not.getSignedValue())
    }

    test("get sum of two integers") {
        val ty = context.getIntType(32)
        val v1 = ConstantInt(ty, 100)
        val v2 = ConstantInt(ty, 300)
        val sum = v1.getAdd(v2)

        assertEquals(400, sum.getSignedValue())
        assertEquals(400, sum.getUnsignedValue())
    }

    test("get difference between two integers") {
        val ty = context.getIntType(32)
        val v1 = ConstantInt(ty, 400)
        val v2 = ConstantInt(ty, 200)
        val diff = v1.getSub(v2)

        assertEquals(200, diff.getSignedValue())
        assertEquals(200, diff.getUnsignedValue())
    }

    test("get the product of two integers") {
        val ty = context.getIntType(32)
        val v1 = ConstantInt(ty, 100)
        val v2 = ConstantInt(ty, 10)
        val product = v1.getMul(v2)

        assertEquals(1000, product.getSignedValue())
        assertEquals(1000, product.getUnsignedValue())
    }

    test("get the quotient of two integers") {
        val ty = context.getIntType(32)
        val v1 = ConstantInt(ty, 100, false)
        val v2 = ConstantInt(ty, 10, false)

        val quotient1 = v1.getSDiv(v2, exact = true)
        val quotient2 = v1.getSDiv(v2, exact = false)
        val quotient3 = v1.getUDiv(v2, exact = true)
        val quotient4 = v1.getUDiv(v2, exact = false)

        assertEquals(10, quotient1.getSignedValue())
        assertEquals(10, quotient2.getSignedValue())
        assertEquals(10, quotient3.getSignedValue())
        assertEquals(10, quotient4.getSignedValue())
    }

    test("get the truncated value from floating point division") {
        val ty = context.getIntType(32)
        // 10 div 3 is not an even number
        val v1 = ConstantInt(ty, 10, false)
        val v2 = ConstantInt(ty, 3, false)

        val quotient1 = v1.getSDiv(v2, exact = true)
        val quotient2 = v1.getSDiv(v2, exact = false)
        val quotient3 = v1.getUDiv(v2, exact = true)
        val quotient4 = v1.getUDiv(v2, exact = false)

        assertEquals(3, quotient1.getSignedValue())
        assertEquals(3, quotient2.getSignedValue())
        assertEquals(3, quotient3.getSignedValue())
        assertEquals(3, quotient4.getSignedValue())
    }

    test("get the remainder of this and another integer") {
        val ty = context.getIntType(32)
        val v1 = ConstantInt(ty, 10, false)
        val v2 = ConstantInt(ty, 3, false)

        val rem1 = v1.getURem(v2)
        val rem2 = v1.getSRem(v2)

        assertEquals(1, rem1.getUnsignedValue())
        assertEquals(1, rem1.getSignedValue())
        assertEquals(1, rem2.getUnsignedValue())
        assertEquals(1, rem2.getSignedValue())
    }

    test("get the logical and result with this and another integer") {
        val i32 = context.getIntType(32)
        val lhs = ConstantInt(i32, 2)
        val rhs = ConstantInt(i32, 6)
        val res = lhs.getAnd(rhs).getSignedValue()

        assertEquals(2 and 6, res)
    }

    test("get the logical or result with this and another integer") {
        val i32 = context.getIntType(32)
        val lhs = ConstantInt(i32, 16)
        val rhs = ConstantInt(i32, 92)
        val res = lhs.getOr(rhs).getSignedValue()

        assertEquals(16 or 92, res)
    }

    test("get the logical xor result with this and another integer") {
        val i32 = context.getIntType(32)
        val lhs = ConstantInt(i32, 100)
        val rhs = ConstantInt(i32, 200)
        val res = lhs.getXor(rhs).getSignedValue()

        assertEquals(100 xor 200, res)
    }

    test("perform comparison of two integers") {
        val i32 = context.getIntType(32)
        val lhs = ConstantInt(i32, 10)
        val rhs = ConstantInt(i32, 20)
        val expected = arrayOf<Long>(
            0, 1, // eq, ne
            0, 0, // ugt, uge
            1, 1, // ult ule
            0, 0, // sgt sge
            1, 1 // slt sle
        )

        runAll(*IntPredicate.values()) { it, idx ->
            val res = lhs.getICmp(it, rhs).getUnsignedValue()

            val expect = expected[idx]

            assertEquals(expect, res)
        }
    }

    test("get this shifted left of another integer") {
        val i32 = context.getIntType(32)
        val lhs = ConstantInt(i32, 10)
        val rhs = ConstantInt(i32, 20)

        val res = lhs.getShl(rhs).getSignedValue()

        assertEquals(10 shl 20, res)
    }

    test("get this shifted right of another integer") {
        val i32 = context.getIntType(32)
        val lhs = ConstantInt(i32, 10)
        val rhs = ConstantInt(i32, 20)

        val res = lhs.getLShr(rhs).getSignedValue()

        assertEquals(10 shr 20, res)
    }

    test("get this arithmetically shifted right of another integer") {
        val i32 = context.getIntType(32)
        val lhs = ConstantInt(i32, 10)
        val rhs = ConstantInt(i32, 20)

        val res = lhs.getAShr(rhs).getSignedValue()

        assertEquals(10 shr 20, res)
    }

    test("truncating to a tinier integer type") {
        val i8 = context.getIntType(8)
        val i1 = context.getIntType(1)
        val lhs = ConstantInt(i8, 64)

        val trunc = lhs.getTrunc(i1)

        assertEquals(0, trunc.getUnsignedValue())
    }

    test("zero or sign-extend to a larger integer type") {
        val i8 = context.getIntType(8)
        val i16 = context.getIntType(16)
        val lhs = ConstantInt(i8, 64)

        val sext = lhs.getSExt(i16)
        val zext = lhs.getZExt(i16)

        assertEquals(64, sext.getSignedValue())
        assertEquals(64, zext.getUnsignedValue())
    }

    test("cast to floating point type") {
        val i64 = context.getIntType(64)
        val float = context.getFloatType(TypeKind.Float)
        val double = context.getFloatType(TypeKind.Double)
        val lhs = ConstantInt(i64, 64)

        val si = lhs.getSIToFP(float)
        val ui = lhs.getUIToFP(double)

        assertEquals(64.0, si.getDouble())
        assertEquals(64.0, ui.getDouble())
        assertFalse { si.getDoubleLosesPrecision() }
        assertFalse { si.getDoubleLosesPrecision() }
    }

    test("cast into pointer type") {
        val ty = context.getIntType(64)
        val lhs = ConstantInt(ty, 100)
        val ptr = lhs.getIntToPtr(ty.getPointerType())

        assertTrue { ptr.isConstant() }

        val num = ptr.getIntCast(ty)

        assertEquals(lhs.getSignedValue(), num.getSignedValue())
    }

    test("cast to different int type") {
        val i32 = context.getIntType(32)
        val targetTy = context.getIntType(128)
        val lhs = ConstantInt(i32, 100000)

        val second = lhs.getIntCast(targetTy, true)

        assertEquals(lhs.getSignedValue(), second.getSignedValue())
    }

    test("casting to its own type does nothing") {
        val i32 = context.getIntType(32)
        val lhs = ConstantInt(i32, 100000)

        lhs.getIntCast(i32, true)
    }

    test("selecting between two values on a condition") {
        val i1 = context.getIntType(1)
        val cond = ConstantInt(i1, 1)

        val i32 = context.getIntType(32)
        val lhs = ConstantInt(i32, 10)
        val rhs = ConstantInt(i32, 20)

        val res = cond.getSelect(lhs, rhs)

        assertEquals(10, cast<ConstantInt>(res).getSignedValue())
    }
})
