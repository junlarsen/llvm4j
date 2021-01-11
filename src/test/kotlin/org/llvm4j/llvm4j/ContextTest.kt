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

        assertFalse { ctx.isDiscardingValueNames() }

        ctx.setDiscardingValueNames(true)

        assertTrue { ctx.isDiscardingValueNames() }
    }

    @Test fun `Test global context is referentially equal`() {
        val subject1 = GlobalContext
        val subject2 = LLVM.LLVMGetGlobalContext()

        assertEquals(GlobalContext.ref, subject1.ref)
        assertEquals(subject2, subject1.ref)
    }
}