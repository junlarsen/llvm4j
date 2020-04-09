package dev.supergrecko.kllvm.core

import dev.supergrecko.kllvm.llvm.typedefs.Context
import dev.supergrecko.kllvm.internal.util.runAll
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ContextTest {
    @Test
    fun `fails to reuse dropped context`() {
        val ctx = Context()
        ctx.dispose()

        assertFailsWith<IllegalArgumentException> {
            ctx.getDiagnosticHandler()
        }
    }

    @Test
    fun `dropping context twice fails`() {
        val ctx = Context()

        ctx.dispose()

        assertFailsWith<IllegalArgumentException> {
            ctx.dispose()
        }
    }

    @Test
    fun `modifying discard value names actually works`() {
        val ctx = Context()

        runAll(true, false) {
            ctx.discardValueNames = it
            assertEquals(it, ctx.discardValueNames)
        }
    }
}
