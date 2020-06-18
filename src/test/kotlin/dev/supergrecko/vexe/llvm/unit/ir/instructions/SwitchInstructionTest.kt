package dev.supergrecko.vexe.llvm.unit.ir.instructions

import dev.supergrecko.vexe.llvm.ir.Builder
import dev.supergrecko.vexe.llvm.ir.Module
import dev.supergrecko.vexe.llvm.ir.types.FunctionType
import dev.supergrecko.vexe.llvm.ir.types.IntType
import dev.supergrecko.vexe.llvm.ir.types.StructType
import dev.supergrecko.vexe.llvm.ir.values.constants.ConstantInt
import dev.supergrecko.vexe.llvm.utils.cleanup
import dev.supergrecko.vexe.test.TestSuite

internal class SwitchInstructionTest : TestSuite({
    describe("Assigning same block to two conditions") {
        val module = Module("test.ll")
        val function = module.addFunction("test", FunctionType(
            StructType(listOf(IntType(1), IntType(1)), false),
            listOf(),
            false
        ))
        val block = function.createBlock("entry")
        val builder = Builder()

        val cond = ConstantInt(IntType(1), 1)
        val inst = builder
            .build()
            .createSwitch(cond, block, 1)

        inst.addCase(ConstantInt(IntType(1), 1), block)

        cleanup(builder, module)
    }

    describe("The expected cases can be passed") {
        val module = Module("test.ll")
        val function = module.addFunction("test", FunctionType(
            StructType(listOf(IntType(1), IntType(1)), false),
            listOf(),
            false
        ))
        val block = function.createBlock("entry")
        val builder = Builder()

        val cond = ConstantInt(IntType(1), 1)
        val inst = builder
            .build()
            .createSwitch(cond, block, 1)

        inst.addCase(ConstantInt(IntType(1), 1), block)
        inst.addCase(ConstantInt(IntType(1), 1), block)
        inst.addCase(ConstantInt(IntType(1), 1), block)
        inst.addCase(ConstantInt(IntType(1), 1), block)

        cleanup(builder, module)
    }
})
