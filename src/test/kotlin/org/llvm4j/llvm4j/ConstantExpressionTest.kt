package org.llvm4j.llvm4j

import org.junit.jupiter.api.Test
import org.llvm4j.optional.None
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ConstantExpressionTest {
    @Test fun `Test basic floating-point constant expressions`() {
        val ctx = Context()
        val float = ctx.getFloatType()
        val lhs = float.getConstant(20.0)
        val rhs = float.getConstant(10.0)

        val res1 = cast<ConstantFP>(ConstantExpression.getFloatNeg(lhs))
        val res2 = cast<ConstantFP>(ConstantExpression.getFloatAdd(lhs, rhs))
        val res3 = cast<ConstantFP>(ConstantExpression.getFloatSub(lhs, rhs))
        val res4 = cast<ConstantFP>(ConstantExpression.getFloatMul(lhs, rhs))
        val res5 = cast<ConstantFP>(ConstantExpression.getFloatDiv(lhs, rhs))
        val res6 = cast<ConstantFP>(ConstantExpression.getFloatRem(lhs, rhs))

        assertEquals(-20.0, res1.getLossyValue())
        assertEquals(30.0, res2.getLossyValue())
        assertEquals(10.0, res3.getLossyValue())
        assertEquals(200.0, res4.getLossyValue())
        assertEquals(2.0, res5.getLossyValue())
        assertEquals(0.0, res6.getLossyValue())

        val expected = mapOf<FloatPredicate, Long>(
            FloatPredicate.True to 1,
            FloatPredicate.False to 0,
            FloatPredicate.OrderedEqual to 0,
            FloatPredicate.OrderedGreaterThan to 1,
            FloatPredicate.OrderedGreaterEqual to 1,
            FloatPredicate.OrderedLessThan to 0,
            FloatPredicate.OrderedLessEqual to 0,
            FloatPredicate.OrderedNotEqual to 1,
            FloatPredicate.Ordered to 1,
            FloatPredicate.Unordered to 0,
            FloatPredicate.UnorderedEqual to 0,
            FloatPredicate.UnorderedGreaterThan to 1,
            FloatPredicate.UnorderedGreaterEqual to 1,
            FloatPredicate.UnorderedLessThan to 0,
            FloatPredicate.UnorderedLessEqual to 0,
            FloatPredicate.UnorderedNotEqual to 1
        )
        for ((k, v) in expected) {
            val res = cast<ConstantInt>(ConstantExpression.getFloatCompare(k, lhs, rhs))
            assertEquals(v, res.getZeroExtendedValue())
        }
    }

    @Test fun `Test basic integer constant expressions`() {
        val ctx = Context()
        val i32 = ctx.getInt32Type()
        val lhs = i32.getConstant(20)
        val rhs = i32.getConstant(10)

        for (semantic in WrapSemantics.values()) {
            val res = cast<ConstantInt>(ConstantExpression.getIntAdd(lhs, rhs, semantic))
            assertEquals(30, res.getZeroExtendedValue())
        }

        for (semantic in WrapSemantics.values()) {
            val res = cast<ConstantInt>(ConstantExpression.getIntSub(lhs, rhs, semantic))
            assertEquals(10, res.getZeroExtendedValue())
        }

        for (semantic in WrapSemantics.values()) {
            val res = cast<ConstantInt>(ConstantExpression.getIntMul(lhs, rhs, semantic))
            assertEquals(200, res.getZeroExtendedValue())
        }

        for (exact in listOf(true, false)) {
            val res1 = cast<ConstantInt>(ConstantExpression.getUnsignedDiv(lhs, rhs, exact))
            val res2 = cast<ConstantInt>(ConstantExpression.getSignedDiv(lhs, rhs, exact))
            assertEquals(2, res1.getZeroExtendedValue())
            assertEquals(2, res2.getZeroExtendedValue())
        }

        val res1 = cast<ConstantInt>(ConstantExpression.getUnsignedRem(lhs, rhs))
        val res2 = cast<ConstantInt>(ConstantExpression.getSignedRem(lhs, rhs))
        val res3 = cast<ConstantInt>(ConstantExpression.getLeftShift(lhs, rhs))
        val res4 = cast<ConstantInt>(ConstantExpression.getLogicalShiftRight(lhs, rhs))
        val res5 = cast<ConstantInt>(ConstantExpression.getArithmeticShiftRight(lhs, rhs))
        val res6 = cast<ConstantInt>(ConstantExpression.getLogicalAnd(lhs, rhs))
        val res7 = cast<ConstantInt>(ConstantExpression.getLogicalOr(lhs, rhs))
        val res8 = cast<ConstantInt>(ConstantExpression.getLogicalXor(lhs, rhs))

        assertEquals(0, res1.getZeroExtendedValue())
        assertEquals(0, res2.getZeroExtendedValue())
        assertEquals(20 shl 10, res3.getZeroExtendedValue())
        assertEquals(20 shr 10, res4.getZeroExtendedValue())
        assertEquals(20 ushr 10, res5.getZeroExtendedValue())
        assertEquals(20 and 10, res6.getZeroExtendedValue())
        assertEquals(20 or 10, res7.getZeroExtendedValue())
        assertEquals(20 xor 10, res8.getZeroExtendedValue())

        val expected = mapOf<IntPredicate, Long>(
            IntPredicate.Equal to 0,
            IntPredicate.NotEqual to 1,
            IntPredicate.UnsignedGreaterThan to 1,
            IntPredicate.UnsignedGreaterEqual to 1,
            IntPredicate.UnsignedLessThan to 0,
            IntPredicate.UnsignedLessEqual to 0,
            IntPredicate.SignedGreaterThan to 1,
            IntPredicate.SignedGreaterEqual to 1,
            IntPredicate.SignedLessThan to 0,
            IntPredicate.SignedLessEqual to 0
        )
        for ((k, v) in expected) {
            val res = cast<ConstantInt>(ConstantExpression.getIntCompare(k, lhs, rhs))
            assertEquals(v, res.getZeroExtendedValue())
        }
    }

    @Test
    fun `Test extract and insert value constant expressions`() {
        val ctx = Context()
        val i8 = ctx.getInt8Type()
        val f32 = ctx.getFloatType()
        val type = ctx.getStructType(f32, i8)
        val f32v = f32.getConstant(100.0)
        val i8v = i8.getConstant(45)
        val struct = type.getConstant(f32v, i8v, isPacked = false).unwrap()
        val subject1 = ConstantExpression.getExtractValue(struct, 0)

        assertTrue { isa<ConstantFP>(subject1) }
        assertEquals(100.0, cast<ConstantFP>(subject1).getValuePair().first)

        val f32v2 = f32.getConstant(42.0)
        val new = cast<ConstantStruct>(ConstantExpression.getInsertValue(struct, f32v2, 0))

        assertEquals(42.0, cast<ConstantFP>(ConstantExpression.getExtractValue(new, 0)).getLossyValue())
    }

    @Test
    fun `Test get element ptr constant expressions`() {
        val ctx = Context()
        val mod = ctx.newModule("test_module")
        val i32 = ctx.getInt32Type()
        val st = ctx.getStructType(i32, i32, isPacked = false)
        val init = st.getConstant(i32.getConstant(1), i32.getConstant(0), isPacked = false)
        val struct = mod.addGlobalVariable("struct", st, None).unwrap().also {
            it.setInitializer(init.unwrap())
        }
        for (inBounds in listOf(true, false)) {
            val zero = i32.getConstant(0)
            val i32ptr = ConstantExpression.getGetElementPtr(struct, zero, zero, inBounds = inBounds)

            assertTrue { isa<ConstantExpression>(i32ptr) }
        }
    }

    @Test
    fun `Test select constant expressions`() {
        val ctx = Context()
        val i1 = ctx.getInt1Type()
        val i32 = ctx.getInt32Type()
        val condition = i1.getConstant(1)
        val ifTrue = i32.getConstant(100)
        val ifFalse = i32.getConstant(200)
        val res = cast<ConstantInt>(ConstantExpression.getSelect(condition, ifTrue, ifFalse))

        assertEquals(100, res.getZeroExtendedValue())
    }
}