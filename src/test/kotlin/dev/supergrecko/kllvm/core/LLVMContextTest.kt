package dev.supergrecko.kllvm.core

import dev.supergrecko.kllvm.utils.runAll
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class LLVMContextTest {
    @Test
    fun `fails to reuse dropped context`() {
        val ctx = LLVMContext.create()
        ctx.dispose()

        assertFailsWith<IllegalArgumentException> {
            ctx.getDiagnosticHandler()
        }
    }

    @Test
    fun `dropping context twice fails`() {
        val ctx = LLVMContext.create()

        ctx.dispose()

        assertFailsWith<IllegalArgumentException> {
            LLVMContext.disposeContext(ctx)
        }
    }

    @Test
    fun `modifying discard value names actually works`() {
        val ctx = LLVMContext.create()

        runAll(true, false) {
            ctx.setDiscardValueNames(it)
            assertEquals(it, ctx.shouldDiscardValueNames())
        }
    }
}
