package org.llvm4j.llvm4j

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ValueTest {
    @Test fun `Test non-global may not have a name`() {
        val ctx = Context()
        val i32 = ctx.getInt32Type()
        val subject = i32.getConstant(100)

        assertEquals("", subject.getName())

        subject.setName("test_val")

        assertEquals("", subject.getName())
    }
}

class ConstantIntTest {
    @Test fun `Test ConstantInt follows specification`() {
        val ctx = Context()
        val i32 = ctx.getInt32Type()
        val subject1 = i32.getConstant(100)
        val subject2 = i32.getConstantNull()
        val subject3 = i32.getConstantPointerNull()
        val subject4 = i32.getConstantUndef()

        assertEquals(i32.ref, subject1.getType().ref)
        assertEquals(ValueKind.ConstantInt, subject1.getValueKind())
        assertEquals("i32 100", subject1.getAsString())
        assertEquals(100, subject1.getSignExtendedValue())
        assertTrue { subject1.isConstant() }
        assertFalse { subject1.isUndef() }
        assertFalse { subject1.isNull() }

        assertTrue { subject2.isNull() }
        assertTrue { subject3.isNull() }
        assertTrue { subject4.isUndef() }
    }

    @Test fun `Test get all ones`() {
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
}

class ConstantFloatTest {
    @Test fun `Test ConstantFloat follows specification`() {
        val ctx = Context()
        val float = ctx.getFloatType()
        val subject1 = float.getConstant(100.0)
        val subject2 = float.getConstantNull()
        val subject3 = float.getConstantPointerNull()
        val subject4 = float.getConstantUndef()

        assertEquals(float.ref, subject1.getType().ref)
        assertEquals(ValueKind.ConstantFP, subject1.getValueKind())
        assertEquals("float 1.000000e+02", subject1.getAsString())
        assertEquals(Pair(100.0, false), subject1.getValue())
        assertTrue { subject1.isConstant() }
        assertFalse { subject1.isUndef() }
        assertFalse { subject1.isNull() }

        assertTrue { subject2.isNull() }
        assertTrue { subject3.isNull() }
        assertTrue { subject4.isUndef() }
    }

    @Test fun `Test get all ones`() {
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
            val subject = type.getAllOnes().getValue()

            assertEquals(value, subject.first)
            assertEquals(lossy, subject.second)
        }
    }
}