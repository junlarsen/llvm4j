package dev.supergrecko.vexe.llvm.unit.ir.instructions

import dev.supergrecko.vexe.llvm.ir.Builder
import dev.supergrecko.vexe.llvm.ir.Module
import dev.supergrecko.vexe.llvm.ir.types.FunctionType
import dev.supergrecko.vexe.llvm.ir.types.IntType
import dev.supergrecko.vexe.llvm.ir.values.constants.ConstantInt
import dev.supergrecko.vexe.llvm.utils.TestSuite
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test

internal class BrInstructionTest : TestSuite() {
    @Test
    fun `Creation of unconditional branch`() {
        val builder = Builder()
        val module = Module("test.ll")
        val function = module.addFunction("test", FunctionType(
            IntType(32), listOf(), false
        ))
        val destination = function.createBlock("entry")

        val inst = builder
            .getInstructionBuilder()
            .createBr(destination)

        assertFalse { inst.isConditional() }

        cleanup(builder)
    }

    @Test
    fun `Creation of conditional branch`() {
        val builder = Builder()
        val module = Module("test.ll")
        val function = module.addFunction("test", FunctionType(
            IntType(32), listOf(), false
        ))
        val then = function.createBlock("then")
        val otherwise = function.createBlock("otherwise")
        val condition = ConstantInt(IntType(1), 1)

        val inst = builder
            .getInstructionBuilder()
            .createCondBr(condition, then, otherwise)
        val cond = ConstantInt(inst.getCondition().ref)

        assertTrue { inst.isConditional() }
        assertEquals(1, cond.getUnsignedValue())

        cleanup(builder)
    }
}
