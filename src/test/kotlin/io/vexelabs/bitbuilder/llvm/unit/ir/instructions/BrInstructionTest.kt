package io.vexelabs.bitbuilder.llvm.unit.ir.instructions

import io.vexelabs.bitbuilder.llvm.ir.Builder
import io.vexelabs.bitbuilder.llvm.ir.Module
import io.vexelabs.bitbuilder.llvm.ir.types.FunctionType
import io.vexelabs.bitbuilder.llvm.ir.types.IntType
import io.vexelabs.bitbuilder.llvm.ir.values.constants.ConstantInt
import io.vexelabs.bitbuilder.llvm.setup
import kotlin.test.assertEquals
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
        val subject = builder.createBr(destination)

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

        val subject = builder.createCondBr(condition, then, otherwise)
        val foundCondition = ConstantInt(subject.getCondition().ref)

        assertEquals(condition.ref, foundCondition.ref)
        assertTrue { subject.isConditional() }
    }
})
