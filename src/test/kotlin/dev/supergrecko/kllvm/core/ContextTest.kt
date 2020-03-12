package dev.supergrecko.kllvm.core

import org.junit.jupiter.api.Test
import java.lang.IllegalArgumentException
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
            Context.disposeContext(ctx)
        }
    }
}