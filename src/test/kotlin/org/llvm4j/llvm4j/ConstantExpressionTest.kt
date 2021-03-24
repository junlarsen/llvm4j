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

        val neg = cast<ConstantFP>(ConstantExpression.getFloatNeg(lhs))
        val add = cast<ConstantFP>(ConstantExpression.getFloatAdd(lhs, rhs))
        val sub = cast<ConstantFP>(ConstantExpression.getFloatSub(lhs, rhs))
        val mul = cast<ConstantFP>(ConstantExpression.getFloatMul(lhs, rhs))
        val div = cast<ConstantFP>(ConstantExpression.getFloatDiv(lhs, rhs))
        val rem = cast<ConstantFP>(ConstantExpression.getFloatRem(lhs, rhs))

        assertEquals(-20.0, neg.getLossyValue())
        assertEquals(30.0, add.getLossyValue())
        assertEquals(10.0, sub.getLossyValue())
        assertEquals(200.0, mul.getLossyValue())
        assertEquals(2.0, div.getLossyValue())
        assertEquals(0.0, rem.getLossyValue())

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

        val res0 = cast<ConstantInt>(ConstantExpression.getIntNeg(lhs))
        assertEquals(-20, res0.getSignExtendedValue())

        for (semantic in WrapSemantics.values()) {
            val add = cast<ConstantInt>(ConstantExpression.getIntAdd(lhs, rhs, semantic))
            assertEquals(30, add.getZeroExtendedValue())
            val sub = cast<ConstantInt>(ConstantExpression.getIntSub(lhs, rhs, semantic))
            assertEquals(10, sub.getZeroExtendedValue())
            val mul = cast<ConstantInt>(ConstantExpression.getIntMul(lhs, rhs, semantic))
            assertEquals(200, mul.getZeroExtendedValue())
        }

        for (exact in listOf(true, false)) {
            val res1 = cast<ConstantInt>(ConstantExpression.getUnsignedDiv(lhs, rhs, exact))
            val res2 = cast<ConstantInt>(ConstantExpression.getSignedDiv(lhs, rhs, exact))
            assertEquals(2, res1.getZeroExtendedValue())
            assertEquals(2, res2.getZeroExtendedValue())
        }

        val urem = cast<ConstantInt>(ConstantExpression.getUnsignedRem(lhs, rhs))
        val srem = cast<ConstantInt>(ConstantExpression.getSignedRem(lhs, rhs))
        val lshift = cast<ConstantInt>(ConstantExpression.getLeftShift(lhs, rhs))
        val lshiftr = cast<ConstantInt>(ConstantExpression.getLogicalShiftRight(lhs, rhs))
        val ashr = cast<ConstantInt>(ConstantExpression.getArithmeticShiftRight(lhs, rhs))
        val logAnd = cast<ConstantInt>(ConstantExpression.getLogicalAnd(lhs, rhs))
        val logOr = cast<ConstantInt>(ConstantExpression.getLogicalOr(lhs, rhs))
        val logXor = cast<ConstantInt>(ConstantExpression.getLogicalXor(lhs, rhs))

        assertEquals(0, urem.getZeroExtendedValue())
        assertEquals(0, srem.getZeroExtendedValue())
        assertEquals(20 shl 10, lshift.getZeroExtendedValue())
        assertEquals(20 shr 10, lshiftr.getZeroExtendedValue())
        assertEquals(20 ushr 10, ashr.getZeroExtendedValue())
        assertEquals(20 and 10, logAnd.getZeroExtendedValue())
        assertEquals(20 or 10, logOr.getZeroExtendedValue())
        assertEquals(20 xor 10, logXor.getZeroExtendedValue())

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
    fun `Test type casting constant expressions`() {
        val ctx = Context()
        val iSmall = ctx.getInt32Type()
        val iLarge = ctx.getInt64Type()
        val fSmall = ctx.getFloatType()
        val fLarge = ctx.getDoubleType()
        val iPtr = ctx.getPointerType(iSmall).unwrap()
        val iPtrAddr = ctx.getPointerType(iSmall, AddressSpace.from(1)).unwrap()
        val iLargePtr = ctx.getPointerType(iLarge).unwrap()
        val iSmallV = iSmall.getConstant(1)
        val iLargeV = iLarge.getConstant(2)
        val fSmallV = fSmall.getConstant(1.0)
        val fLargeV = fLarge.getConstant(2.0)

        val intTrunc = cast<ConstantInt>(ConstantExpression.getIntTrunc(iLargeV, iSmall))
        assertEquals(iSmall.ref, intTrunc.getType().ref)
        val zeroExt = cast<ConstantInt>(ConstantExpression.getZeroExt(iSmallV, iLarge))
        assertEquals(iLarge.ref, zeroExt.getType().ref)
        val signExt = cast<ConstantInt>(ConstantExpression.getSignExt(iSmallV, iLarge))
        assertEquals(iLarge.ref, signExt.getType().ref)
        val fpTrunc = cast<ConstantFP>(ConstantExpression.getFloatTrunc(fLargeV, fSmall))
        assertEquals(fSmall.ref, fpTrunc.getType().ref)
        val fpExt = cast<ConstantFP>(ConstantExpression.getFloatExt(fSmallV, fLarge))
        assertEquals(fLarge.ref, fpExt.getType().ref)
        val fpToUi = cast<ConstantInt>(ConstantExpression.getFloatToUnsigned(fSmallV, iSmall))
        assertEquals(iSmall.ref, fpToUi.getType().ref)
        val fpToSi = cast<ConstantInt>(ConstantExpression.getFloatToSigned(fSmallV, iSmall))
        assertEquals(iSmall.ref, fpToSi.getType().ref)
        val uiToFp = cast<ConstantFP>(ConstantExpression.getUnsignedToFloat(iSmallV, fSmall))
        assertEquals(fSmall.ref, uiToFp.getType().ref)
        val siToFp = cast<ConstantFP>(ConstantExpression.getSignedToFloat(iSmallV, fSmall))
        assertEquals(fSmall.ref, siToFp.getType().ref)

        val ptr = cast<ConstantExpression>(ConstantExpression.getIntToPointer(iSmallV, iPtr))
        assertEquals(iPtr.ref, ptr.getType().ref)
        assertEquals(Opcode.IntToPtr, ptr.getOpcode())
        val int = cast<ConstantInt>(ConstantExpression.getPointerToInt(ptr, iSmall))
        assertEquals(iSmall.ref, int.getType().ref)

        val bitcast = cast<ConstantExpression>(ConstantExpression.getBitCast(ptr, iLargePtr))
        assertEquals(iLargePtr.ref, bitcast.getType().ref)
        val addrspacecast = cast<ConstantExpression>(ConstantExpression.getAddrSpaceCast(ptr, iPtrAddr))
        assertEquals(1, PointerType(addrspacecast.getType().ref).getAddressSpace().value)
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
    fun `Test vector manipulation expressions`() {
        val ctx = Context()
        val i32 = ctx.getInt32Type()
        val vec1 = i32.getConstantVector(i32.getConstant(0), i32.getConstant(1))
        val vec2 = i32.getConstantVector(i32.getConstant(2), i32.getConstant(3))
        val mask = i32.getConstantVector(i32.getConstant(0), i32.getConstant(4))
        val sum = cast<ConstantVector>(ConstantExpression.getShuffleVector(vec1, vec2, mask))

        assertEquals(2, sum.getOperandCount())

        val subject1 = cast<ConstantInt>(ConstantExpression.getExtractElement(vec1, i32.getConstant(1)))
        assertEquals(1, subject1.getZeroExtendedValue())
        val vec = cast<ConstantDataVector>(ConstantExpression.getInsertElement(vec1, i32.getConstant(100), i32.getConstant(1)))
        val subject2 = cast<ConstantInt>(ConstantExpression.getExtractElement(vec, i32.getConstant(1)))
        assertEquals(100, subject2.getZeroExtendedValue())
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
