package org.llvm4j.llvm4j

import org.junit.jupiter.api.Test
import org.llvm4j.llvm4j.testing.assertIsErr
import org.llvm4j.llvm4j.testing.assertIsNone
import org.llvm4j.llvm4j.testing.assertIsOk
import org.llvm4j.llvm4j.testing.assertIsSome
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class IntrinsicFunctionTest {
    @Test fun `Test looking up intrinsics`() {
        val subject1 = IntrinsicFunction.lookup("llvm.va_start")
        val subject2 = IntrinsicFunction.lookup("doesnt_exist")
        val subject3 = IntrinsicFunction.lookup("llvm.ctpop.v4i8")

        assertIsSome(subject1)
        assertIsNone(subject2)
        assertIsSome(subject3)
    }

    @Test fun `Test non-overloaded intrinsics`() {
        val ctx = Context()
        val mod = ctx.newModule("test_module")
        val subject1 = IntrinsicFunction.lookup("llvm.va_start").get()

        assertFalse { subject1.isOverloaded() }
        assertEquals("llvm.va_start", subject1.getName())
        assertIsErr(subject1.getOverloadedName())
        assertIsErr(subject1.getOverloadedDeclaration(mod))
        assertIsErr(subject1.getOverloadedType(ctx))

        val res1 = subject1.getDeclaration(mod)
        val res2 = subject1.getType(ctx)

        assertIsOk(res1)
        assertIsOk(res2)

        val subject2 = res1.get()
        val subject3 = res2.get()

        assertEquals("llvm.va_start", subject2.getName())
        assertEquals(1, subject3.getParameterCount())
    }

    @Test fun `Test overloaded intrinsics`() {
        val ctx = Context()
        val mod = ctx.newModule("test_module")
        val i8 = ctx.getInt8Type()
        val v4i8 = ctx.getVectorType(i8, 4).get()
        val subject1 = IntrinsicFunction.lookup("llvm.ctpop").get()

        assertTrue { subject1.isOverloaded() }
        assertIsOk(subject1.getOverloadedName(v4i8))
        assertIsErr(subject1.getType(ctx))
        assertIsErr(subject1.getDeclaration(mod))
        assertEquals("llvm.ctpop", subject1.getName())
        assertEquals("llvm.ctpop.v4i8", subject1.getOverloadedName(v4i8).get())

        val res1 = subject1.getOverloadedDeclaration(mod, v4i8)
        val res2 = subject1.getOverloadedType(ctx, v4i8)

        assertIsOk(res1)
        assertIsOk(res2)

        val subject2 = res1.get()
        val subject3 = res2.get()

        assertEquals("llvm.ctpop.v4i8", subject2.getName())
        assertEquals(1, subject3.getParameterCount())
        assertEquals(v4i8.ref, subject3.getParameterTypes().first().ref)
    }
}
