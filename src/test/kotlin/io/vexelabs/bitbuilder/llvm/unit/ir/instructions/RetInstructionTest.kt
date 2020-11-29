package io.vexelabs.bitbuilder.llvm.unit.ir.instructions

import io.vexelabs.bitbuilder.llvm.ir.Builder
import io.vexelabs.bitbuilder.llvm.ir.Context
import io.vexelabs.bitbuilder.llvm.ir.Module
import io.vexelabs.bitbuilder.llvm.ir.Opcode
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
    val context: Context by memoized()

    test("create void return") {
        val inst = builder.createRetVoid()

        assertTrue { inst.isTerminator() }
        assertNull(inst.getInstructionBlock())

        assertTrue { module.verify(VerifierFailureAction.ReturnStatus) }
    }

    test("create a value return") {
        val i32 = context.getIntType(32)
        val value = i32.getConstant(0)
        val inst = builder.createRet(value)

        assertTrue { inst.isTerminator() }
        assertEquals(Opcode.Ret, inst.getOpcode())
        assertTrue { module.verify(VerifierFailureAction.ReturnStatus) }
    }

    test("create aggregate return") {
        val i1 = context.getIntType(1)
        val struct = context.getStructType(i1, i1, packed = false)
        val fnTy = context.getFunctionType(struct, variadic = false)
        val function = module.createFunction("test", fnTy)
        val block = function.createBlock("entry")
        builder.setPositionAtEnd(block)

        val left = i1.getConstant(1)
        val right = i1.getConstant(0)
        val inst = builder.createAggregateRet(listOf(left, right))

        val ir = "  ret { i1, i1 } { i1 true, i1 false }"

        assertEquals(ir, inst.getIR().toString())
        assertTrue { module.verify(VerifierFailureAction.ReturnStatus) }
    }
})
