package dev.supergrecko.kllvm.unit.ir

import dev.supergrecko.kllvm.utils.KLLVMTestCase
import dev.supergrecko.kllvm.utils.runAll
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import org.bytedeco.javacpp.Pointer
import org.bytedeco.llvm.LLVM.LLVMDiagnosticHandler
import org.bytedeco.llvm.LLVM.LLVMDiagnosticInfoRef
import org.junit.jupiter.api.Test

internal class ContextTest : KLLVMTestCase() {
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
        val handler = object : LLVMDiagnosticHandler() {
            override fun call(p0: LLVMDiagnosticInfoRef?, p1: Pointer?) {
            }
        }
        val ctx = Context().apply {
            setDiagnosticHandler(handler)
        }
        val res: LLVMDiagnosticHandler? = ctx.getDiagnosticHandler()

        assertNotNull(res)

        cleanup(ctx)
    }

    @Test
    fun `Mutating discard value names`() {
        val ctx = Context()

        runAll(true, false) { it, _ ->
            ctx.setDiscardValueNames(it)
            assertEquals(it, ctx.isDiscardingValueNames())
        }

        cleanup(ctx)
    }
}
