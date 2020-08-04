package dev.supergrecko.vexe.llvm.unit.ir.instructions

import dev.supergrecko.vexe.llvm.ir.Builder
import dev.supergrecko.vexe.llvm.ir.Module
import dev.supergrecko.vexe.llvm.ir.types.FunctionType
import dev.supergrecko.vexe.llvm.ir.types.IntType
import dev.supergrecko.vexe.llvm.ir.values.constants.ConstantInt
import dev.supergrecko.vexe.llvm.setup
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.spekframework.spek2.Spek

internal class BrInstructionTest : Spek({
    setup()

    val builder: Builder by memoized()
    val module: Module by memoized()

    test("create unconditional branch") {
        val function = module.createFunction(
            "test", FunctionType(
                IntType(32), listOf(), false
            )
        )
        val destination = function.createBlock("Entry")
        val subject = builder.build()
            .createBr(destination)

        assertFalse { subject.isConditional() }
    }

    test("create conditional branch") {
        val function = module.createFunction(
            "test", FunctionType(
                IntType(32), listOf(), false
            )
        )
        val then = function.createBlock("then")
        val otherwise = function.createBlock("otherwise")
        // i1 true
        val condition = ConstantInt(IntType(1), 1)

        val subject = builder
            .build()
            .createCondBr(condition, then, otherwise)
        val foundCondition = ConstantInt(subject.getCondition().ref)

        assertTrue { subject.isConditional() }
    }
})
