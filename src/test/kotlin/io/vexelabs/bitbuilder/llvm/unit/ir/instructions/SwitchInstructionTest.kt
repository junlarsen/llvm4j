package io.vexelabs.bitbuilder.llvm.unit.ir.instructions

import io.vexelabs.bitbuilder.llvm.ir.Builder
import io.vexelabs.bitbuilder.llvm.ir.Context
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
    val context: Context by memoized()

    test("assigning same block to two conditions is valid") {
        val i1 = context.getIntType(1)
        val struct = context.getStructType(i1, i1, packed = false)
        val fnTy = context.getFunctionType(struct, variadic = false)
        val function = module.createFunction("test", fnTy)
        val block = function.createBlock("entry")
        val cond = i1.getConstant(1)
        val inst = builder.createSwitch(cond, block, 1)

        inst.addCase(i1.getConstant(1), block)
    }

    test("you may exceed the expected amount of cases") {
        val i1 = context.getIntType(1)
        val struct = context.getStructType(i1, i1, packed = false)
        val fnTy = context.getFunctionType(struct, variadic = false)
        val function = module.createFunction("test", fnTy)
        val block = function.createBlock("entry")
        val cond = i1.getConstant(1)
        val inst = builder.createSwitch(cond, block, expectedCases = 1)

        for (i in 0..10) {
            inst.addCase(i1.getConstant(1), block)
        }
    }
})
