package dev.supergrecko.vexe.llvm.unit.ir.instructions

import dev.supergrecko.vexe.llvm.ir.Builder
import dev.supergrecko.vexe.llvm.ir.Module
import dev.supergrecko.vexe.llvm.ir.types.FunctionType
import dev.supergrecko.vexe.llvm.ir.types.IntType
import dev.supergrecko.vexe.llvm.setup
import dev.supergrecko.vexe.llvm.support.VerifierFailureAction
import org.spekframework.spek2.Spek
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

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
        val instr = builder.build().createIndirectBr(base)

        assertNotNull(instr)
    }
})
