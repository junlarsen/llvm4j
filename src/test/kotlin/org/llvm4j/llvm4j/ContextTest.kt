package org.llvm4j.llvm4j

import org.bytedeco.javacpp.IntPointer
import org.bytedeco.llvm.global.LLVM
import org.junit.jupiter.api.Test
import org.llvm4j.llvm4j.testing.assertIsSome
import org.llvm4j.llvm4j.util.None
import org.llvm4j.llvm4j.util.Some
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ContextTest {
    @Test fun `Test the diagnostic handler and payload`() {
        val ctx = Context()
        val handler = Context.DiagnosticHandler { }
        val payload = IntPointer(42)

        assertEquals(None, ctx.getDiagnosticPayload())

        ctx.setDiagnosticHandler(handler, Some(payload))

        assertIsSome(ctx.getDiagnosticPayload())
        assertEquals(Some(payload), ctx.getDiagnosticPayload())
    }

    @Test fun `Test value names are discarded`() {
        val ctx = Context()
        val i8 = ctx.getInt32Type()
        val void = ctx.getVoidType()
        val fnTy = ctx.getFunctionType(void, i8)
        val mod = ctx.createModule("test_module")
        val subject1 = mod.addFunction("test_fn", fnTy)
        val (subject2) = subject1.getParameters()

        subject2.setName("arg1")

        assertFalse { ctx.isDiscardingValueNames() }
        assertEquals("arg1", subject2.getName())

        ctx.setDiscardingValueNames(true)
        subject2.setName("other_name")
        // ignored subject2.setName
        assertEquals("arg1", subject2.getName())
        assertTrue { ctx.isDiscardingValueNames() }

        val subject3 = mod.addFunction("test_fn2", fnTy)
        val (subject4) = subject3.getParameters()

        assertEquals("", subject4.getName())

        subject4.setName("arg")

        assertEquals("", subject4.getName())
        assertEquals("test_fn2", subject3.getName())
    }

    @Test fun `Test global context is referentially equal`() {
        val subject1 = GlobalContext
        val subject2 = LLVM.LLVMGetGlobalContext()

        assertEquals(GlobalContext.ref, subject1.ref)
        assertEquals(subject2, subject1.ref)
    }
}
