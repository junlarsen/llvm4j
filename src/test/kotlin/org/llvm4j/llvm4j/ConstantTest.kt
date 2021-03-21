package org.llvm4j.llvm4j

import org.junit.jupiter.api.Test
import org.llvm4j.llvm4j.testing.assertIsErr
import org.llvm4j.llvm4j.testing.assertIsOk
import org.llvm4j.optional.None
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ConstantIntTest {
    @Test
    fun `Test ConstantInt properties`() {
        val ctx = Context()
        val i32 = ctx.getInt32Type()
        val subject1 = i32.getConstant(100)
        val subject2 = i32.getConstantNull()
        val subject3 = i32.getConstantPointerNull()
        val subject4 = i32.getConstantUndef()
        val subject5 = i32.getConstant(-100)

        assertEquals(i32.ref, subject1.getType().ref)
        assertEquals(ValueKind.ConstantInt, subject1.getValueKind())
        assertEquals("i32 100", subject1.getAsString())
        assertEquals(100, subject1.getSignExtendedValue())
        assertEquals(100, subject1.getZeroExtendedValue())
        assertTrue { subject1.isConstant() }
        assertFalse { subject1.isUndef() }
        assertFalse { subject1.isNull() }

        assertTrue { subject2.isNull() }
        assertTrue { subject3.isNull() }
        assertTrue { subject4.isUndef() }
        assertEquals(2 * (Int.MAX_VALUE.toLong() + 1) - 100, subject5.getZeroExtendedValue())
        assertEquals(-100, subject5.getSignExtendedValue())
    }

    @Test
    fun `Test get all ones`() {
        val ctx = Context()
        val i1 = ctx.getInt1Type()
        val i8 = ctx.getInt8Type()
        val i16 = ctx.getInt16Type()
        val i32 = ctx.getInt32Type()
        val i64 = ctx.getInt64Type()
        val i128 = ctx.getInt128Type()

        for (type in listOf(i1, i8, i16, i32, i64, i128)) {
            val subject = type.getAllOnes()
            assertEquals(-1, subject.getSignExtendedValue())
        }
    }

    @Test
    fun `Test integer math constant expressions`() {
        val ctx = Context()
        val i32 = ctx.getInt32Type()
        val lhs = i32.getConstant(20)
        val rhs = i32.getConstant(10)

        for (semantic in WrapSemantics.values()) {
            val res = cast<ConstantInt>(lhs.getIntAdd(rhs, semantic))
            assertEquals(30, res.getZeroExtendedValue())
        }

        for (semantic in WrapSemantics.values()) {
            val res = cast<ConstantInt>(lhs.getIntSub(rhs, semantic))
            assertEquals(10, res.getZeroExtendedValue())
        }

        for (semantic in WrapSemantics.values()) {
            val res = cast<ConstantInt>(lhs.getIntMul(rhs, semantic))
            assertEquals(200, res.getZeroExtendedValue())
        }

        for (exact in listOf(true, false)) {
            val res1 = cast<ConstantInt>(lhs.getUnsignedDiv(rhs, exact))
            val res2 = cast<ConstantInt>(lhs.getSignedDiv(rhs, exact))
            assertEquals(2, res1.getZeroExtendedValue())
            assertEquals(2, res2.getZeroExtendedValue())
        }

        val res1 = cast<ConstantInt>(lhs.getUnsignedRem(rhs))
        val res2 = cast<ConstantInt>(lhs.getSignedRem(rhs))
        val res3 = cast<ConstantInt>(lhs.getLeftShift(rhs))
        val res4 = cast<ConstantInt>(lhs.getLogicalShiftRight(rhs))
        val res5 = cast<ConstantInt>(lhs.getArithmeticShiftRight(rhs))
        val res6 = cast<ConstantInt>(lhs.getLogicalAnd(rhs))
        val res7 = cast<ConstantInt>(lhs.getLogicalOr(rhs))
        val res8 = cast<ConstantInt>(lhs.getLogicalXor(rhs))

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
            val res = cast<ConstantInt>(lhs.getIntCompare(k, rhs))
            assertEquals(v, res.getZeroExtendedValue())
        }
    }
}

class ConstantFPTest {
    @Test
    fun `Test ConstantFloat properties`() {
        val ctx = Context()
        val float = ctx.getFloatType()
        val subject1 = float.getConstant(100.0)
        val subject2 = float.getConstantNull()
        val subject3 = float.getConstantPointerNull()
        val subject4 = float.getConstantUndef()

        assertEquals(float.ref, subject1.getType().ref)
        assertEquals(ValueKind.ConstantFP, subject1.getValueKind())
        assertEquals("float 1.000000e+02", subject1.getAsString())
        assertEquals(Pair(100.0, false), subject1.getValuePair())
        assertTrue { subject1.isConstant() }
        assertFalse { subject1.isUndef() }
        assertFalse { subject1.isNull() }

        assertTrue { subject2.isNull() }
        assertTrue { subject3.isNull() }
        assertTrue { subject4.isUndef() }
    }

    @Test
    fun `Test get all ones`() {
        val ctx = Context()
        val float = ctx.getFloatType()
        val bfloat = ctx.getBFloatType()
        val double = ctx.getDoubleType()
        val x86fp80 = ctx.getX86FP80Type()
        val fp128 = ctx.getFP128Type()
        val ppcfp128 = ctx.getPPCFP128Type()
        val expected = mapOf(
            float to Pair(Double.NaN, false),
            bfloat to Pair(Double.NaN, false),
            double to Pair(Double.NaN, false),
            x86fp80 to Pair(Double.NaN, true),
            fp128 to Pair(Double.NaN, true),
            ppcfp128 to Pair(Double.NaN, false)
        )

        for ((type, result) in expected) {
            val (value, lossy) = result
            val subject = type.getAllOnes().getValuePair()

            assertEquals(value, subject.first)
            assertEquals(lossy, subject.second)
        }
    }

    @Test
    fun `Test floating point math constant expressions`() {
        val ctx = Context()
        val float = ctx.getFloatType()
        val lhs = float.getConstant(20.0)
        val rhs = float.getConstant(10.0)

        val res1 = cast<ConstantFP>(lhs.getFloatNeg())
        val res2 = cast<ConstantFP>(lhs.getFloatAdd(rhs))
        val res3 = cast<ConstantFP>(lhs.getFloatSub(rhs))
        val res4 = cast<ConstantFP>(lhs.getFloatMul(rhs))
        val res5 = cast<ConstantFP>(lhs.getFloatDiv(rhs))
        val res6 = cast<ConstantFP>(lhs.getFloatRem(rhs))

        assertEquals(-20.0, res1.getValuePair().first)
        assertEquals(30.0, res2.getValuePair().first)
        assertEquals(10.0, res3.getValuePair().first)
        assertEquals(200.0, res4.getValuePair().first)
        assertEquals(2.0, res5.getValuePair().first)
        assertEquals(0.0, res6.getValuePair().first)

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
            val res = cast<ConstantInt>(lhs.getFloatCompare(k, rhs))
            assertEquals(v, res.getZeroExtendedValue())
        }
    }
}

class ConstantArrayTest {
    @Test
    fun `Test ConstantArray properties`() {
        val ctx = Context()
        val i8 = ctx.getInt8Type()
        val a4i8 = ctx.getArrayType(i8, 4).unwrap()
        val values = (0..10).map { i8.getConstant(it) }.toTypedArray()
        val subject1 = i8.getConstantArray(*values)
        val subject2 = a4i8.getConstantNull()

        assertEquals(ValueKind.ConstantDataArray, subject1.getValueKind())
        assertEquals("[11 x i8] c\"\\00\\01\\02\\03\\04\\05\\06\\07\\08\\09\\0A\"", subject1.getAsString())
        assertTrue { subject1.isConstant() }
        assertFalse { subject1.isUndef() }
        assertFalse { subject1.isNull() }

        assertTrue { subject2.isConstant() }
        assertTrue { subject2.isNull() }
        assertFalse { subject2.isUndef() }
    }
}

class ConstantVectorTest {
    @Test
    fun `Test ConstantVector properties`() {
        val ctx = Context()
        val i8 = ctx.getInt8Type()
        val v4i8 = ctx.getVectorType(i8, 4).unwrap()
        val values = (0..10).map { i8.getConstant(it) }.toTypedArray()
        val subject1 = i8.getConstantVector(*values)
        val subject2 = v4i8.getConstantNull()

        assertEquals(ValueKind.ConstantDataVector, subject1.getValueKind())
        assertEquals("<11 x i8> <i8 0, i8 1, i8 2, i8 3, i8 4, i8 5, i8 6, i8 7, i8 8, i8 9, i8 10>", subject1.getAsString())
        assertTrue { subject1.isConstant() }
        assertFalse { subject1.isUndef() }
        assertFalse { subject1.isNull() }

        assertTrue { subject2.isConstant() }
        assertTrue { subject2.isNull() }
        assertFalse { subject2.isUndef() }
    }
}

class ConstantPointerNullTest {
    @Test
    fun `Test ConstantPointerNull properties`() {
        val ctx = Context()
        val i32 = ctx.getInt32Type()
        val i32ptr = ctx.getPointerType(i32).unwrap()
        val subject1 = i32.getConstantPointerNull()
        val subject2 = i32ptr.getConstantNull()

        assertEquals(ValueKind.ConstantPointerNull, subject1.getValueKind())
        assertEquals("i32 null", subject1.getAsString())
        assertEquals("i32* null", subject2.getAsString())
        assertTrue { subject1.isConstant() }
        assertTrue { subject1.isNull() }
        assertFalse { subject1.isUndef() }
    }
}

class ConstantStructTest {
    @Test
    fun `Test anonymous ConstantStruct properties`() {
        val ctx = Context()
        val i8 = ctx.getInt8Type()
        val f32 = ctx.getFloatType()
        val type = ctx.getStructType(f32, i8)
        val f32v = f32.getConstant(100.0)
        val i8v = i8.getConstant(45)
        val res = type.getConstant(f32v, i8v, isPacked = false)

        assertIsOk(res)

        val subject1 = res.unwrap()
        val subject2 = type.getConstantNull()

        assertEquals(ValueKind.ConstantStruct, subject1.getValueKind())
        assertEquals("{ float, i8 } { float 1.000000e+02, i8 45 }", subject1.getAsString())
        assertTrue { subject1.isConstant() }
        assertFalse { subject1.isNull() }
        assertFalse { subject1.isUndef() }

        assertTrue { subject2.isConstant() }
        assertTrue { subject2.isNull() }
        assertFalse { subject2.isUndef() }
    }

    @Test
    fun `Test named ConstantStruct properties`() {
        val ctx = Context()
        val i8 = ctx.getInt8Type()
        val f32 = ctx.getFloatType()
        val type = ctx.getNamedStructType("struct_t")
        val f32v = f32.getConstant(100.0)
        val i8v = i8.getConstant(45)
        val res1 = type.getConstant(f32v, i8v, isPacked = false)

        // Struct type is opaque, cannot make constant
        assertIsErr(res1)

        type.setElementTypes(f32, i8)

        val res2 = type.getConstant(f32v, i8v, isPacked = false)

        assertIsOk(res2)

        val subject1 = res2.unwrap()

        assertEquals(ValueKind.ConstantStruct, subject1.getValueKind())
        assertEquals("{ float, i8 } { float 1.000000e+02, i8 45 }", subject1.getAsString())
        assertTrue { subject1.isConstant() }
        assertFalse { subject1.isNull() }
        assertFalse { subject1.isUndef() }
    }

    @Test
    fun `Test struct constant expressions`() {
        val ctx = Context()
        val i32 = ctx.getInt32Type()
        val i8 = ctx.getInt8Type()
        val f32 = ctx.getFloatType()
        val type = ctx.getStructType(f32, i8)
        val f32v = f32.getConstant(100.0)
        val i8v = i8.getConstant(45)
        val struct = type.getConstant(f32v, i8v, isPacked = false).unwrap()
        val subject1 = struct.getExtractValue(0)

        assertTrue { isa<ConstantFP>(subject1) }
        assertEquals(100.0, cast<ConstantFP>(subject1).getValuePair().first)

        val f32v2 = f32.getConstant(42.0)
        val new = cast<ConstantStruct>(struct.getInsertValue(f32v2, 0))

        assertEquals(42.0, cast<ConstantFP>(new.getExtractValue(0)).getValuePair().first)
    }

    @Test
    fun `Test getelementptr constexpr`() {
        val ctx = Context()
        val mod = ctx.newModule("test_module")
        val i32 = ctx.getInt32Type()
        val st = ctx.getStructType(i32, i32, isPacked = false)
        val init = st.getConstant(i32.getConstant(1), i32.getConstant(0), isPacked = false)
        val v = mod.addGlobalVariable("struct", st, None).unwrap().also {
            it.setInitializer(init.unwrap())
        }

        val x = v
    }
}
