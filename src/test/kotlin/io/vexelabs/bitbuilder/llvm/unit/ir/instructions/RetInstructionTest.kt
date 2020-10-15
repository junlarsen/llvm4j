package io.vexelabs.bitbuilder.llvm.unit.ir.instructions

import io.vexelabs.bitbuilder.llvm.ir.Builder
import io.vexelabs.bitbuilder.llvm.ir.Module
import io.vexelabs.bitbuilder.llvm.ir.Opcode
import io.vexelabs.bitbuilder.llvm.ir.types.FunctionType
import io.vexelabs.bitbuilder.llvm.ir.types.IntType
import io.vexelabs.bitbuilder.llvm.ir.types.StructType
import io.vexelabs.bitbuilder.llvm.ir.values.constants.ConstantInt
import io.vexelabs.bitbuilder.llvm.setup
import io.vexelabs.bitbuilder.llvm.support.VerifierFailureAction
import org.spekframework.spek2.Spek
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

internal class RetInstructionTest : Spek({
    setup()

    val module: Module by memoized()
    val builder: Builder by memoized()

    test("create void return") {
        val inst = builder.createRetVoid()

        assertTrue { inst.isTerminator() }
        assertNull(inst.getInstructionBlock())

        assertTrue { module.verify(VerifierFailureAction.ReturnStatus) }
    }

    test("create a value return") {
        val value = ConstantInt(IntType(32), 0)
        val inst = builder.createRet(value)

        assertTrue { inst.isTerminator() }
        assertEquals(Opcode.Ret, inst.getOpcode())
        assertTrue { module.verify(VerifierFailureAction.ReturnStatus) }
    }

    test("create aggregate return") {
        val function = module.createFunction(
            "test",
            FunctionType(
                StructType(listOf(IntType(1), IntType(1)), false),
                listOf(),
                false
            )
        )
        val block = function.createBlock("entry")
        builder.setPositionAtEnd(block)

        val left = ConstantInt(IntType(1), 1)
        val right = ConstantInt(IntType(1), 0)
        val inst = builder.createAggregateRet(listOf(left, right))

        val ir = "  ret { i1, i1 } { i1 true, i1 false }"

        assertEquals(ir, inst.getIR().toString())
        assertTrue { module.verify(VerifierFailureAction.ReturnStatus) }
    }
})
