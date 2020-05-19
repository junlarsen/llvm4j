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
    fun `Attempting to dispose twice fails`() {
        val ctx = Context()
        ctx.dispose()

        assertFailsWith<IllegalArgumentException> {
            ctx.getDiagnosticHandler()
        }
    }

    @Test
    fun `Passing a callback hook`() {
        // TODO: Rewrite these with kt lambdas
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
    fun `Mutating discard value names`() {
        val ctx = Context()

        runAll(true, false) { it, _ ->
            ctx.setDiscardValueNames(it)
            assertEquals(it, ctx.isDiscardingValueNames())
        }
    }
}
