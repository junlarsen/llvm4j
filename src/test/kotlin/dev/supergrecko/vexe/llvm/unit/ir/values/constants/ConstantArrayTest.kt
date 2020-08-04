package dev.supergrecko.vexe.llvm.unit.ir.values.constants

import dev.supergrecko.vexe.llvm.ir.types.IntType
import dev.supergrecko.vexe.llvm.ir.values.constants.ConstantArray
import dev.supergrecko.vexe.llvm.ir.values.constants.ConstantInt
import dev.supergrecko.vexe.llvm.utils.constIntPairOf
import org.spekframework.spek2.Spek
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class ConstantArrayTest : Spek({
    test("create string as constant i8 array") {
        val str = ConstantArray("Hello")

        assertTrue { str.isConstantString() }

        // LLVM strips the null-termination
        assertEquals("Hello", str.getAsString())
    }

    test("create a constant array") {
        val ty = IntType(8)
        val (one, two) = constIntPairOf(1, 2)
        val arr = ConstantArray(ty, listOf(one, two))

        val first = arr.getElementAsConstant(0)

        assertEquals(
            one.getSignedValue(),
            ConstantInt(first.ref).getSignedValue()
        )
    }
})
