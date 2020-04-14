package dev.supergrecko.kllvm.ir

import dev.supergrecko.kllvm.internal.util.runAll
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import org.junit.jupiter.api.Test

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
