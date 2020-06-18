package dev.supergrecko.vexe.llvm.unit.ir.instructions

import dev.supergrecko.vexe.llvm.ir.Builder
import dev.supergrecko.vexe.llvm.ir.Module
import dev.supergrecko.vexe.llvm.ir.Opcode
import dev.supergrecko.vexe.llvm.ir.types.FunctionType
import dev.supergrecko.vexe.llvm.ir.types.IntType
import dev.supergrecko.vexe.llvm.ir.types.StructType
import dev.supergrecko.vexe.llvm.ir.values.constants.ConstantInt
import dev.supergrecko.vexe.llvm.utils.cleanup
import dev.supergrecko.vexe.test.TestSuite
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

internal class RetInstructionTest : TestSuite({
    describe("Creation of ret void instruction") {
        val builder = Builder()
        val inst = builder
            .build()
            .createRetVoid()

        assertTrue { inst.isTerminator() }
        assertNull(inst.getInstructionBlock())

        cleanup(builder)
    }

    describe("Creation of ret i32 0 instruction") {
        val builder = Builder()
        val value = ConstantInt(IntType(32), 0)
        val inst = builder
            .build()
            .createRet(value)

        assertTrue { inst.isTerminator() }
        assertEquals(Opcode.Ret, inst.getOpcode())

        cleanup(builder)
    }

    describe("Creation of aggregate ret") {
        val module = Module("test.ll")
        val function = module.addFunction("test", FunctionType(
            StructType(listOf(IntType(1), IntType(1)), false),
            listOf(),
            false
        ))
        val block = function.createBlock("entry")
        val builder = Builder().apply {
            setPositionAtEnd(block)
        }

        val left = ConstantInt(IntType(1), 1)
        val right = ConstantInt(IntType(1), 0)
        val inst = builder
            .build()
            .createAggregateRet(listOf(left, right))

        val ir = "  ret { i1, i1 } { i1 true, i1 false }"
        assertEquals(ir, inst.dumpToString())

        cleanup(builder, module)
    }
})
