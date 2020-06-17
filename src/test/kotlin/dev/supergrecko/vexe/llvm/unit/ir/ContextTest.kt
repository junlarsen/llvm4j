package dev.supergrecko.vexe.llvm.unit.ir

import dev.supergrecko.vexe.llvm.ir.Context
import dev.supergrecko.vexe.llvm.utils.cleanup
import dev.supergrecko.vexe.llvm.utils.runAll
import dev.supergrecko.vexe.test.TestSuite
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import org.bytedeco.javacpp.Pointer
import org.bytedeco.llvm.LLVM.LLVMDiagnosticHandler
import org.bytedeco.llvm.LLVM.LLVMDiagnosticInfoRef

internal class ContextTest : TestSuite({
    describe("Attempting to dispose twice fails") {
        val ctx = Context()
        ctx.dispose()

        assertFailsWith<IllegalArgumentException> {
            ctx.getDiagnosticHandler()
        }
    }

    describe("Passing a callback hook") {
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

    describe("Mutating discard value names") {
        val ctx = Context()

        runAll(true, false) { it, _ ->
            ctx.setDiscardValueNames(it)
            assertEquals(it, ctx.isDiscardingValueNames())
        }

        cleanup(ctx)
    }
})
