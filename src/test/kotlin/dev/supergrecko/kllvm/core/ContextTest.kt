package dev.supergrecko.kllvm.core

import org.bytedeco.llvm.global.LLVM
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
            Context.disposeContext(ctx)
        }
    }

    @Test
    fun `modifying discard value names actually works`() {
        val ctx = Context.create()
        val values = listOf(true, false)

        values.forEach {
            ctx.setDiscardValueNames(it)
            assertEquals(it, ctx.shouldDiscardValueNames())
        }
    }

    @Test
    fun `get integer types from llvm`() {
        val ctx = Context.create()
        val sizes = listOf(1, 8, 16, 32, 64, 128, /* LLVMGetIntType */ 256, 1024)

        sizes.forEach {
            val type = ctx.iType(it)
            assertEquals(it, LLVM.LLVMGetIntTypeWidth(type))
        }
    }
}