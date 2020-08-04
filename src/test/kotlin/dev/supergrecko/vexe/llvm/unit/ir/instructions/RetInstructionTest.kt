package dev.supergrecko.vexe.llvm.unit.ir.instructions

import dev.supergrecko.vexe.llvm.ir.Builder
import dev.supergrecko.vexe.llvm.ir.Module
import dev.supergrecko.vexe.llvm.ir.Opcode
import dev.supergrecko.vexe.llvm.ir.types.FunctionType
import dev.supergrecko.vexe.llvm.ir.types.IntType
import dev.supergrecko.vexe.llvm.ir.types.StructType
import dev.supergrecko.vexe.llvm.ir.values.constants.ConstantInt
import dev.supergrecko.vexe.llvm.setup
import dev.supergrecko.vexe.llvm.support.VerifierFailureAction
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import org.spekframework.spek2.Spek

internal class RetInstructionTest : Spek({
    setup()

    val module: Module by memoized()
    val builder: Builder by memoized()

    test("create void return") {
        val inst = builder
            .build()
            .createRetVoid()

        assertTrue { inst.isTerminator() }
        assertNull(inst.getInstructionBlock())

        assertTrue { module.verify(VerifierFailureAction.ReturnStatus) }
    }

    test("create a value return") {
        val value = ConstantInt(IntType(32), 0)
        val inst = builder
            .build()
            .createRet(value)

        assertTrue { inst.isTerminator() }
        assertEquals(Opcode.Ret, inst.getOpcode())
        assertTrue { module.verify(VerifierFailureAction.ReturnStatus) }
    }

    test("create aggregate return") {
        val function = module.createFunction(
            "test", FunctionType(
                StructType(listOf(IntType(1), IntType(1)), false),
                listOf(),
                false
            )
        )
        val block = function.createBlock("entry")
        builder.setPositionAtEnd(block)

        val left = ConstantInt(IntType(1), 1)
        val right = ConstantInt(IntType(1), 0)
        val inst = builder
            .build()
            .createAggregateRet(listOf(left, right))

        val ir = "  ret { i1, i1 } { i1 true, i1 false }"

        assertEquals(ir, inst.getIR().toString())
        assertTrue { module.verify(VerifierFailureAction.ReturnStatus) }
    }
})
