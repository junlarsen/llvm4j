package dev.supergrecko.kllvm.ir

import dev.supergrecko.kllvm.test.runAll
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import org.bytedeco.javacpp.Pointer
import org.bytedeco.llvm.LLVM.LLVMDiagnosticHandler
import org.bytedeco.llvm.LLVM.LLVMDiagnosticInfoRef
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
    fun `attempting to dispose twice will fail but not cause segfault`() {
        val ctx = Context()

        ctx.dispose()

        assertFailsWith<IllegalArgumentException> {
            ctx.dispose()
        }
    }

    @Test
    fun `setting the diagnostic handler`() {
        val ctx = Context()

        val handler = object : LLVMDiagnosticHandler() {
            override fun call(p0: LLVMDiagnosticInfoRef?, p1: Pointer?) {
            }
        }

        ctx.setDiagnosticHandler(handler)

        val res: LLVMDiagnosticHandler? = ctx.getDiagnosticHandler()

        assertNotNull(res)

        ctx.dispose()
    }

    @Test
    fun `the discardValueNames property is functional`() {
        val ctx = Context()

        runAll(true, false) { it, _ ->
            ctx.setDiscardValueNames(it)
            assertEquals(it, ctx.isDiscardingValueNames())
        }
    }
}
