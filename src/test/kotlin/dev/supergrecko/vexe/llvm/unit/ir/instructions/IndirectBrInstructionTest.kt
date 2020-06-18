package dev.supergrecko.vexe.llvm.unit.ir.instructions

import dev.supergrecko.vexe.llvm.ir.Builder
import dev.supergrecko.vexe.llvm.ir.Module
import dev.supergrecko.vexe.llvm.ir.types.FunctionType
import dev.supergrecko.vexe.llvm.ir.types.IntType
import dev.supergrecko.vexe.llvm.utils.cleanup
import dev.supergrecko.vexe.test.TestSuite

internal class IndirectBrInstructionTest : TestSuite({
    describe("Indirect Branching") {
        val builder = Builder()
        val module = Module("test.ll")
        val function = module.addFunction(
            "test", FunctionType(
                IntType(32),
                listOf(), false
            )
        )
        val base = function.createBlock("Entry").toValue()

        describe("Creating a basic break") {
            builder.build()
                .createIndirectBr(base)
        }

        cleanup(module, builder)
    }
})
