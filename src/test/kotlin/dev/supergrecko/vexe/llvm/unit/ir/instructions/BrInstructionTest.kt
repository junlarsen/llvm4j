package dev.supergrecko.vexe.llvm.unit.ir.instructions

import dev.supergrecko.vexe.llvm.ir.Builder
import dev.supergrecko.vexe.llvm.ir.Module
import dev.supergrecko.vexe.llvm.ir.types.FunctionType
import dev.supergrecko.vexe.llvm.ir.types.IntType
import dev.supergrecko.vexe.llvm.ir.values.constants.ConstantInt
import dev.supergrecko.vexe.llvm.utils.cleanup
import dev.supergrecko.vexe.test.TestSuite
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.junit.jupiter.api.Assertions.assertFalse

internal class BrInstructionTest : TestSuite({
    describe("Br Instruction Suite") {
        val builder = Builder()
        val module = Module("test.ll")

        val function = module.createFunction(
            "test", FunctionType(
                IntType(32), listOf(), false
            )
        )

        describe("Creationg of regular unconditional branch") {
            val destination = function.createBlock("Entry")
            val subject = builder.build()
                .createBr(destination)

            assertFalse { subject.isConditional() }
        }

        describe("Creation of conditional branch") {
            val then = function.createBlock("then")
            val otherwise = function.createBlock("otherwise")
            // i1 true
            val condition = ConstantInt(IntType(1), 1)

            val subject = builder
                .build()
                .createCondBr(condition, then, otherwise)
            val foundCondition = ConstantInt(subject.getCondition().ref)

            assertTrue { subject.isConditional() }
            assertEquals(1, foundCondition.getUnsignedValue())
        }

        cleanup(module, builder)
    }
})
