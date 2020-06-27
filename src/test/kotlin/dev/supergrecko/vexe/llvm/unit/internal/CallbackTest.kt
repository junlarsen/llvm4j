package dev.supergrecko.vexe.llvm.unit.internal

import dev.supergrecko.vexe.llvm.ir.Context
import dev.supergrecko.vexe.llvm.utils.cleanup
import dev.supergrecko.vexe.test.TestSuite

internal class CallbackTest : TestSuite({
    describe("Creating a callback") {
        val ctx = Context().apply {
            // Kotlin lambdas!
            setDiagnosticHandler {}
        }

        cleanup(ctx)
    }
})
