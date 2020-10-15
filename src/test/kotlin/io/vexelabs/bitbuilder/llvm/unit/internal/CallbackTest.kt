package io.vexelabs.bitbuilder.llvm.unit.internal

import io.vexelabs.bitbuilder.llvm.ir.Context
import io.vexelabs.bitbuilder.llvm.setup
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
