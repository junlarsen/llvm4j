package dev.supergrecko.kllvm.core

import dev.supergrecko.kllvm.utils.runAll
import org.bytedeco.llvm.global.LLVM
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

    @Test
    fun `get integer types from llvm`() {
        val ctx = LLVMContext.create()

        runAll(1, 6, 16, 32, 64, 8237, 64362) {
            val type = ctx.integerType(it)
            assertEquals(it, type.typeWidth())
        }
    }
}
