package dev.supergrecko.kllvm.core

import dev.supergrecko.kllvm.core.typedefs.Context
import dev.supergrecko.kllvm.utils.runAll
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ContextTest {
    @Test
    fun `fails to reuse dropped context`() {
        val ctx = Context.create()
        ctx.dispose()

        assertFailsWith<IllegalArgumentException> {
            ctx.getDiagnosticHandler()
        }
    }

    @Test
    fun `dropping context twice fails`() {
        val ctx = Context.create()

        ctx.dispose()

        assertFailsWith<IllegalArgumentException> {
            ctx.dispose()
        }
    }

    @Test
    fun `modifying discard value names actually works`() {
        val ctx = Context.create()

        runAll(true, false) {
            ctx.setDiscardValueNames(it)
            assertEquals(it, ctx.shouldDiscardValueNames())
        }
    }
}
