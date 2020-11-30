package io.vexelabs.bitbuilder.llvm.unit.ir.instructions

import io.vexelabs.bitbuilder.llvm.ir.Builder
import io.vexelabs.bitbuilder.llvm.ir.Context
import io.vexelabs.bitbuilder.llvm.ir.Module
import io.vexelabs.bitbuilder.llvm.setup
import org.spekframework.spek2.Spek
import kotlin.test.assertNotNull

internal class IndirectBrInstructionTest : Spek({
    setup()

    val module: Module by memoized()
    val builder: Builder by memoized()
    val context: Context by memoized()

    test("create indirect branch") {
        val i32 = context.getIntType(32)
        val fnTy = context.getFunctionType(i32, variadic = false)
        val function = module.createFunction("test", fnTy)
        val base = function.createBlock("Entry").toValue()
        val instr = builder.createIndirectBr(base)

        assertNotNull(instr)
    }
})
