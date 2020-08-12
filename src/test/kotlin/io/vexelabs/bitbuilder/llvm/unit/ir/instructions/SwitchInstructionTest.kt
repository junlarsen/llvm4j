package io.vexelabs.bitbuilder.llvm.unit.ir.instructions

import io.vexelabs.bitbuilder.llvm.ir.Builder
import io.vexelabs.bitbuilder.llvm.ir.Module
import io.vexelabs.bitbuilder.llvm.ir.types.FunctionType
import io.vexelabs.bitbuilder.llvm.ir.types.IntType
import io.vexelabs.bitbuilder.llvm.ir.types.StructType
import io.vexelabs.bitbuilder.llvm.ir.values.constants.ConstantInt
import io.vexelabs.bitbuilder.llvm.setup
import org.spekframework.spek2.Spek

internal class SwitchInstructionTest : Spek({
    setup()

    val module: Module by memoized()
    val builder: Builder by memoized()

    test("assigning same block to two conditions is valid") {
        val function = module.createFunction("test", FunctionType(
            StructType(listOf(IntType(1), IntType(1)), false),
            listOf(),
            false
        ))
        val block = function.createBlock("entry")
        val cond = ConstantInt(IntType(1), 1)
        val inst = builder
            .build()
            .createSwitch(cond, block, 1)

        inst.addCase(ConstantInt(IntType(1), 1), block)
    }

    test("you may exceed the expected amount of cases") {
        val function = module.createFunction("test", FunctionType(
            StructType(listOf(IntType(1), IntType(1)), false),
            listOf(),
            false
        ))
        val block = function.createBlock("entry")
        val cond = ConstantInt(IntType(1), 1)
        val inst = builder
            .build()
            .createSwitch(cond, block, 1)

        for (i in 0..10) {
            inst.addCase(ConstantInt(IntType(1), i), block)
        }
    }
})
