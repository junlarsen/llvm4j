package dev.supergrecko.vexe.llvm.unit.ir.instructions

import dev.supergrecko.vexe.llvm.ir.Builder
import dev.supergrecko.vexe.llvm.ir.Opcode
import dev.supergrecko.vexe.llvm.ir.types.IntType
import dev.supergrecko.vexe.llvm.ir.values.constants.ConstantInt
import dev.supergrecko.vexe.llvm.utils.VexeLLVMTestCase
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

internal class RetInstructionTest : VexeLLVMTestCase() {
    @Test
    fun `Creation of ret void instruction`() {
        val builder = Builder()
        val inst = builder
            .getInstructionBuilder()
            .createRetVoid()

        assertTrue { inst.isTerminator() }
        assertNull(inst.getInstructionBlock())

        cleanup(builder)
    }

    @Test
    fun `Creation of ret i32 0 instruction`() {
        val builder = Builder()
        val value = ConstantInt(IntType(32), 0)
        val inst = builder
            .getInstructionBuilder()
            .createRet(value)

        assertTrue { inst.isTerminator() }
        assertEquals(Opcode.Ret, inst.getOpcode())

        cleanup(builder)
    }
}