package dev.supergrecko.vexe.llvm.unit.internal

import dev.supergrecko.vexe.llvm.ir.Context
import dev.supergrecko.vexe.llvm.setup
import org.bytedeco.llvm.global.LLVM
import org.spekframework.spek2.Spek
import kotlin.test.assertNotNull

internal object CallbackTest : Spek({
    setup()

    val context: Context by memoized()

    test("creating a callback properly assigns it") {
        context.setDiagnosticHandler { }

        val ptr = LLVM.LLVMContextGetDiagnosticHandler(context.ref)

        assertNotNull(ptr)
    }
})
