package io.vexelabs.bitbuilder.llvm.unit.ir.instructions

import io.vexelabs.bitbuilder.llvm.ir.Builder
import io.vexelabs.bitbuilder.llvm.ir.Module
import io.vexelabs.bitbuilder.llvm.ir.types.FunctionType
import io.vexelabs.bitbuilder.llvm.ir.types.IntType
import io.vexelabs.bitbuilder.llvm.setup
import kotlin.test.assertNotNull
import org.spekframework.spek2.Spek

internal class IndirectBrInstructionTest : Spek({
    setup()

    val module: Module by memoized()
    val builder: Builder by memoized()

    test("create indirect branch") {
        val function = module.createFunction(
            "test", FunctionType(
                IntType(32),
                listOf(), false
            )
        )
        val base = function.createBlock("Entry").toValue()
        val instr = builder.createIndirectBr(base)

        assertNotNull(instr)
    }
})
