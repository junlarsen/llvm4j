package dev.supergrecko.kllvm.core

import dev.supergrecko.kllvm.core.type.FloatingPointTypes
import dev.supergrecko.kllvm.utils.runAll
import org.bytedeco.llvm.LLVM.LLVMTypeRef
import org.bytedeco.llvm.global.LLVM
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

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

        runAll(true, false) {
            ctx.setDiscardValueNames(it)
            assertEquals(it, ctx.shouldDiscardValueNames())
        }
    }

    @Test
    fun `get integer types from llvm`() {
        val ctx = Context.create()

        runAll(1, 6, 16, 32, 64, 8237, 64362) {
            val type = ctx.intType(it)
            assertEquals(it, LLVM.LLVMGetIntTypeWidth(type))
        }
    }
}