package org.llvm4j.llvm4j

import org.junit.jupiter.api.Test
import org.llvm4j.llvm4j.testing.assertIsErr
import org.llvm4j.llvm4j.testing.assertIsNone
import org.llvm4j.llvm4j.testing.assertIsOk
import org.llvm4j.llvm4j.testing.assertIsSome
import org.llvm4j.llvm4j.util.None
import org.llvm4j.llvm4j.util.Some
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TypeTest {
    @Test fun `Test integer types`() {
        val ctx = Context()
        val i1 = ctx.getInt1Type()
        val i8 = ctx.getInt8Type()
        val i16 = ctx.getInt16Type()
        val i32 = ctx.getInt32Type()
        val i64 = ctx.getInt64Type()
        val i128 = ctx.getInt128Type()

        val subject2 = ctx.getIntegerType(256)
        val subject3 = ctx.getIntegerType(99999999)

        assertIsOk(subject2)
        assertIsErr(subject3)

        for (subject in listOf(i1, i8, i16, i32, i64, i128)) {
            assertTrue { subject.isIntegerType() }
            assertTrue { subject.isValidPointerElementType() }
            assertTrue { subject.isValidVectorElementType() }
            assertTrue { subject.isValidArrayElementType() }
            assertEquals("i${subject.getTypeWidth()}", subject.getAsString())

            assertTrue { subject.isSized() }
            assertEquals(ctx.ref, subject.getContext().ref)
        }
    }

    @Test fun `Test floating point types`() {
        val ctx = Context()
        val half = ctx.getHalfType()
        val float = ctx.getFloatType()
        val bfloat = ctx.getBFloatType()
        val fp128 = ctx.getFP128Type()
        val ppcfp128 = ctx.getPPCFP128Type()
        val x86fp80 = ctx.getX86FP80Type()
        val double = ctx.getDoubleType()

        for (subject in listOf(half, float, bfloat, fp128, ppcfp128, x86fp80, double)) {
            assertTrue { subject.isFloatingPointType() }
            assertTrue { subject.isValidPointerElementType() }
            assertTrue { subject.isValidArrayElementType() }
            assertTrue { subject.isValidVectorElementType() }

            assertTrue { subject.isSized() }
            assertEquals(ctx.ref, subject.getContext().ref)
        }
    }

    @Test fun `Test array types`() {
        val ctx = Context()
        val i16 = ctx.getInt16Type()
        val void = ctx.getVoidType()
        val res1 = ctx.getArrayType(i16, 32)
        val res2 = ctx.getArrayType(void, 16)

        assertIsOk(res1)
        assertIsErr(res2)

        val subject1 = res1.unwrap()

        assertTrue { subject1.isArrayType() }
        assertTrue { subject1.isValidPointerElementType() }
        assertTrue { subject1.isValidArrayElementType() }
        assertFalse { subject1.isValidVectorElementType() }
        assertEquals("[32 x i16]", subject1.getAsString())

        assertTrue { subject1.isSized() }
        assertEquals(ctx.ref, subject1.getContext().ref)
        assertEquals(32, subject1.getElementCount())
        assertEquals(i16.ref, subject1.getElementType().ref)
        assertEquals(32, subject1.getSubtypes().size)
        assertEquals(i16.ref, subject1.getSubtypes().first().ref)
    }

    @Test fun `Test vector types`() {
        val ctx = Context()
        val i32 = ctx.getInt32Type()
        val token = ctx.getTokenType()
        val res1 = ctx.getVectorType(i32, 16)
        val res2 = ctx.getVectorType(token, 32)

        assertIsOk(res1)
        assertIsErr(res2)

        val subject1 = res1.unwrap()

        assertTrue { subject1.isVectorType() }
        assertTrue { subject1.isValidPointerElementType() }
        assertTrue { subject1.isValidArrayElementType() }
        assertTrue { subject1.isFixedVectorType() }
        assertFalse { subject1.isScalableVectorType() }
        assertFalse { subject1.isValidVectorElementType() }
        assertEquals("<16 x i32>", subject1.getAsString())

        assertTrue { subject1.isSized() }
        assertEquals(ctx.ref, subject1.getContext().ref)
        assertEquals(16, subject1.getElementCount())
        assertEquals(i32.ref, subject1.getElementType().ref)
        assertEquals(16, subject1.getSubtypes().size)
        assertEquals(i32.ref, subject1.getSubtypes().first().ref)
    }

    @Test fun `Test literal struct types`() {
        val ctx = Context()
        val i8 = ctx.getInt8Type()
        val f32 = ctx.getFloatType()
        val subject1 = ctx.getStructType(f32, i8)
        val subject2 = ctx.getStructType(f32, i8, isPacked = true)

        assertTrue { subject1.isStructType() }
        assertTrue { subject1.isValidPointerElementType() }
        assertTrue { subject1.isValidArrayElementType() }
        assertFalse { subject1.isValidVectorElementType() }
        assertEquals("{ float, i8 }", subject1.getAsString())

        assertTrue { subject1.isLiteral() }
        assertTrue { subject1.isSized() }
        assertIsErr(subject1.getElementType(1000))
        assertFalse { subject1.isPacked() }
        assertFalse { subject1.isOpaque() }
        assertEquals(ctx.ref, subject1.getContext().ref)
        assertEquals(2, subject1.getElementCount())
        assertEquals(listOf(f32.ref, i8.ref), subject1.getElementTypes().map { it.ref })
        assertEquals(f32.ref, subject1.getElementType(0).unwrap().ref)
        assertEquals(i8.ref, subject1.getElementType(1).unwrap().ref)

        assertTrue { subject2.isPacked() }
        assertTrue { subject2.isLiteral() }
        assertTrue { subject2.isSized() }
        assertFalse { subject2.isOpaque() }
    }

    @Test fun `Test named struct types`() {
        val ctx = Context()
        val i8 = ctx.getInt8Type()
        val f32 = ctx.getFloatType()
        val subject1 = ctx.getNamedStructType("struct_t")

        assertTrue { subject1.isStructType() }
        assertTrue { subject1.isValidPointerElementType() }
        assertTrue { subject1.isValidArrayElementType() }
        assertFalse { subject1.isValidVectorElementType() }
        assertEquals("struct_t", subject1.getName())

        assertTrue { subject1.isOpaque() }
        assertIsErr(subject1.getElementTypes())
        assertIsNone(subject1.getElementCount())
        assertIsErr(subject1.getElementType(1000))
        assertFalse { subject1.isLiteral() }
        assertFalse { subject1.isPacked() }
        assertFalse { subject1.isSized() }
        assertEquals(None, subject1.getElementCount())

        val subject2 = subject1.setElementTypes(i8, f32, isPacked = true)
        val subject3 = subject1.setElementTypes(i8)

        assertTrue { subject1.isPacked() }
        assertTrue { subject1.isSized() }
        assertIsSome(subject1.getElementCount())
        assertIsOk(subject1.getElementTypes())
        assertIsErr(subject1.getElementType(1000))
        assertFalse { subject1.isOpaque() }
        assertFalse { subject1.isLiteral() }
        assertEquals(Some(2), subject1.getElementCount())
        assertEquals(listOf(i8.ref, f32.ref), subject1.getElementTypes().unwrap().map { it.ref })
        assertEquals(i8.ref, subject1.getElementType(0).unwrap().ref)
        assertEquals(f32.ref, subject1.getElementType(1).unwrap().ref)

        assertIsOk(subject2)
        assertIsErr(subject3)
    }

    @Test fun `Test function types`() {
        val ctx = Context()
        val i8 = ctx.getInt8Type()
        val void = ctx.getVoidType()
        val subject1 = ctx.getFunctionType(void, i8, isVariadic = false)
        val subject2 = ctx.getFunctionType(void, i8, isVariadic = true)
        val subject3 = ctx.getFunctionType(void, i8, i8, i8)

        assertTrue { subject1.isFunctionType() }
        assertTrue { subject1.isValidPointerElementType() }
        assertFalse { subject1.isValidVectorElementType() }
        assertFalse { subject1.isValidArrayElementType() }
        assertEquals("void (i8)", subject1.getAsString())

        assertFalse { subject1.isVariadic() }
        assertFalse { subject1.isSized() }
        assertEquals(void.ref, subject1.getReturnType().ref)
        assertEquals(1, subject1.getParameterCount())
        assertEquals(i8.ref, subject1.getParameterTypes().first().ref)

        assertTrue { subject2.isVariadic() }
        assertEquals(3, subject3.getParameterCount())
    }

    @Test fun `Test pointer types`() {
        val ctx = Context()
        val i8 = ctx.getInt8Type()
        val void = ctx.getVoidType()
        val res1 = ctx.getPointerType(i8)
        val res2 = ctx.getPointerType(i8, AddressSpace.Other(5))
        val res3 = ctx.getPointerType(void)

        assertIsOk(res1)
        assertIsOk(res2)
        assertIsErr(res3)

        val subject1 = res1.unwrap()
        val subject2 = res2.unwrap()

        assertTrue { subject1.isPointerType() }
        assertTrue { subject1.isValidPointerElementType() }
        assertTrue { subject1.isValidVectorElementType() }
        assertTrue { subject1.isValidArrayElementType() }
        assertEquals("i8*", subject1.getAsString())

        assertTrue { subject1.isSized() }
        assertEquals(1, subject1.getElementCount())
        assertEquals(0, subject1.getAddressSpace().value)
        assertEquals(i8.ref, subject1.getElementType().ref)
        assertEquals(listOf(i8.ref), subject1.getSubtypes().map { it.ref })
        assertEquals(5, subject2.getAddressSpace().value)
    }

    @Test fun `Test token type`() {
        val ctx = Context()
        val subject = ctx.getTokenType()

        assertFalse { subject.isPointerType() }
        assertFalse { subject.isValidPointerElementType() }
        assertFalse { subject.isValidVectorElementType() }
        assertFalse { subject.isValidArrayElementType() }
        assertEquals("token", subject.getAsString())

        assertFalse { subject.isSized() }
        assertEquals(ctx.ref, subject.getContext().ref)
    }

    @Test fun `Test label type`() {
        val ctx = Context()
        val subject = ctx.getLabelType()

        assertFalse { subject.isPointerType() }
        assertFalse { subject.isValidPointerElementType() }
        assertFalse { subject.isValidVectorElementType() }
        assertFalse { subject.isValidArrayElementType() }
        assertEquals("label", subject.getAsString())

        assertFalse { subject.isSized() }
        assertEquals(ctx.ref, subject.getContext().ref)
    }

    @Test fun `Test metadata type`() {
        val ctx = Context()
        val subject = ctx.getMetadataType()

        assertFalse { subject.isPointerType() }
        assertFalse { subject.isValidPointerElementType() }
        assertFalse { subject.isValidVectorElementType() }
        assertFalse { subject.isValidArrayElementType() }
        assertEquals("metadata", subject.getAsString())

        assertFalse { subject.isSized() }
        assertEquals(ctx.ref, subject.getContext().ref)
    }

    @Test fun `Test x86_mmx type`() {
        val ctx = Context()
        val subject = ctx.getX86MMXType()

        assertTrue { subject.isX86MMXType() }
        assertTrue { subject.isValidPointerElementType() }
        assertFalse { subject.isValidVectorElementType() }
        assertTrue { subject.isValidArrayElementType() }
        assertEquals("x86_mmx", subject.getAsString())

        assertTrue { subject.isSized() }
        assertEquals(ctx.ref, subject.getContext().ref)
    }
}
